const apiUrl = "/api/encuentros";
const createForm = document.querySelector("#createForm");
const lookupForm = document.querySelector("#lookupForm");
const lookupId = document.querySelector("#encuentroId");
const notice = document.querySelector("#notice");
const emptyState = document.querySelector("#emptyState");
const encounterCard = document.querySelector("#encuentroCard");

function showNotice(message, isError = false) {
    notice.textContent = message;
    notice.classList.toggle("error", isError);
    notice.hidden = false;
}

function clearNotice() {
    notice.hidden = true;
    notice.textContent = "";
    notice.classList.remove("error");
}

async function requestEncounter(url, options = {}) {
    const response = await fetch(url, {
        ...options,
        headers: { "Content-Type": "application/json", ...options.headers }
    });
    const data = await response.json().catch(() => null);

    if (!response.ok) {
        throw new Error(data?.error || data?.message || `La solicitud falló (HTTP ${response.status}).`);
    }
    return data;
}

function renderEncounter(encounter) {
    document.querySelector("#detalleId").textContent = encounter.id;
    document.querySelector("#detalleDeporte").textContent = encounter.deporte;
    document.querySelector("#detalleFecha").textContent = new Date(encounter.fechaHora).toLocaleString("es-PE", {
        dateStyle: "medium",
        timeStyle: "short"
    });
    document.querySelector("#detalleCancha").textContent = encounter.cancha;
    document.querySelector("#detalleOrganizador").textContent = encounter.organizador;
    document.querySelector("#detalleCupos").textContent = `${encounter.cupoMinimo}–${encounter.cupoMaximo}`;
    document.querySelector("#detalleParticipantes").textContent = encounter.participantes.length
        ? encounter.participantes.join(", ")
        : "Todavía no hay participantes";
    document.querySelector("#estado").textContent = encounter.estado.replaceAll("_", " ");

    emptyState.hidden = true;
    encounterCard.hidden = false;
}

function setSubmitting(form, isSubmitting, idleText) {
    const button = form.querySelector("button[type='submit']");
    button.disabled = isSubmitting;
    button.textContent = isSubmitting ? "Procesando…" : idleText;
}

createForm.addEventListener("submit", async (event) => {
    event.preventDefault();
    clearNotice();

    const formData = new FormData(createForm);
    const minimo = Number(formData.get("cupoMinimo"));
    const maximo = Number(formData.get("cupoMaximo"));
    if (minimo > maximo) {
        showNotice("El cupo mínimo no puede superar al máximo.", true);
        return;
    }

    const payload = {
        deporte: formData.get("deporte").trim(),
        fechaHora: formData.get("fechaHora"),
        cancha: formData.get("cancha").trim(),
        cupoMinimo: minimo,
        cupoMaximo: maximo,
        organizador: formData.get("organizador").trim()
    };

    setSubmitting(createForm, true, "Crear encuentro →");
    try {
        const encounter = await requestEncounter(apiUrl, {
            method: "POST",
            body: JSON.stringify(payload)
        });
        lookupId.value = encounter.id;
        renderEncounter(encounter);
        showNotice(`Encuentro creado. Guarda el ID ${encounter.id} para consultarlo después.`);
    } catch (error) {
        showNotice(error.message, true);
    } finally {
        setSubmitting(createForm, false, "Crear encuentro →");
    }
});

lookupForm.addEventListener("submit", async (event) => {
    event.preventDefault();
    clearNotice();
    const id = lookupId.value.trim();
    if (!id) return;

    setSubmitting(lookupForm, true, "Buscar");
    try {
        const encounter = await requestEncounter(`${apiUrl}/${encodeURIComponent(id)}`);
        renderEncounter(encounter);
        showNotice(`Encuentro ${encounter.id} cargado.`);
    } catch (error) {
        showNotice(error.message, true);
    } finally {
        setSubmitting(lookupForm, false, "Buscar");
    }
});

const fechaInput = document.querySelector("#fechaHora");
const ahora = new Date();
ahora.setMinutes(ahora.getMinutes() - ahora.getTimezoneOffset());
fechaInput.min = ahora.toISOString().slice(0, 16);

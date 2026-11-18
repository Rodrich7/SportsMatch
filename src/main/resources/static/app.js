const apiUrl = "/api/encuentros";
const createForm = document.querySelector("#createForm");
const lookupForm = document.querySelector("#lookupForm");
const lookupId = document.querySelector("#encuentroId");
const notice = document.querySelector("#notice");
const emptyState = document.querySelector("#emptyState");
const encounterCard = document.querySelector("#encuentroCard");
const actionButtons = document.querySelector("#actionButtons");
const participantForm = document.querySelector("#participantForm");

const actionsByState = {
    BORRADOR: [["abrir-inscripciones", "Abrir inscripciones"], ["cancelar", "Cancelar encuentro"]],
    ABIERTO: [["cerrar-inscripciones", "Cerrar inscripciones"], ["cancelar", "Cancelar encuentro"]],
    CERRADO: [["confirmar", "Confirmar encuentro"], ["cancelar", "Cancelar encuentro"]],
    CONFIRMADO: [["iniciar", "Iniciar encuentro"], ["cancelar", "Cancelar encuentro"]],
    EN_JUEGO: [["finalizar", "Finalizar encuentro"], ["cancelar", "Cancelar encuentro"]]
};

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
    renderActions(encounter.estado);

    emptyState.hidden = true;
    encounterCard.hidden = false;
}

function renderActions(state) {
    actionButtons.replaceChildren();
    participantForm.hidden = state !== "ABIERTO";

    const actions = actionsByState[state] || [];
    if (actions.length === 0) {
        const message = document.createElement("span");
        message.className = "no-actions";
        message.textContent = state === "FINALIZADO"
            ? "El encuentro ya finalizó."
            : "El encuentro está cancelado.";
        actionButtons.append(message);
        return;
    }

    for (const [action, label] of actions) {
        const button = document.createElement("button");
        button.className = action === "cancelar" ? "button button-cancel" : "button button-action";
        button.type = "button";
        button.dataset.action = action;
        button.textContent = label;
        actionButtons.append(button);
    }
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

actionButtons.addEventListener("click", async (event) => {
    const button = event.target.closest("button[data-action]");
    if (!button) return;

    clearNotice();
    const id = document.querySelector("#detalleId").textContent;
    button.disabled = true;
    try {
        const encounter = await requestEncounter(`${apiUrl}/${id}/${button.dataset.action}`, { method: "POST" });
        renderEncounter(encounter);
        showNotice(`Estado actualizado: ${encounter.estado.replaceAll("_", " ")}.`);
    } catch (error) {
        showNotice(error.message, true);
        button.disabled = false;
    }
});

participantForm.addEventListener("submit", async (event) => {
    event.preventDefault();
    clearNotice();
    const button = participantForm.querySelector("button[type='submit']");
    button.disabled = true;
    try {
        const participant = new FormData(participantForm).get("participante").trim();
        const id = document.querySelector("#detalleId").textContent;
        const encounter = await requestEncounter(`${apiUrl}/${id}/participantes`, {
            method: "POST",
            body: JSON.stringify({ participante: participant })
        });
        participantForm.reset();
        renderEncounter(encounter);
        showNotice(`${participant} ya está inscrito.`);
    } catch (error) {
        showNotice(error.message, true);
    } finally {
        button.disabled = false;
    }
});

const fechaInput = document.querySelector("#fechaHora");
const ahora = new Date();
ahora.setMinutes(ahora.getMinutes() - ahora.getTimezoneOffset());
fechaInput.min = ahora.toISOString().slice(0, 16);

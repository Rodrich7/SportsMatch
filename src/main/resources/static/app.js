const apiUrl = "/api/encuentros";
const createForm = document.querySelector("#createForm");
const lookupForm = document.querySelector("#lookupForm");
const lookupId = document.querySelector("#encuentroId");
const notice = document.querySelector("#notice");
const emptyState = document.querySelector("#emptyState");
const encounterCard = document.querySelector("#encuentroCard");
const actionButtons = document.querySelector("#actionButtons");
const participantForm = document.querySelector("#participantForm");
const actionNotice = document.querySelector("#actionNotice");
let lookupSequence = 0;

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

function showActionNotice(message, isError = false) {
    actionNotice.textContent = message;
    actionNotice.classList.toggle("error", isError);
    actionNotice.hidden = false;
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
    const fechaHora = formData.get("fechaHora");
    const minimo = Number(formData.get("cupoMinimo"));
    const maximo = Number(formData.get("cupoMaximo"));

    const deporte = formData.get("deporte").trim();
    const cancha = formData.get("cancha").trim();
    const organizador = formData.get("organizador").trim();

    if (!deporte || !fechaHora || !cancha || !organizador || !minimo || !maximo) {
        showNotice("Completa deporte, fecha y hora, cancha, cupos y organizador.", true);
        createForm.querySelector(":invalid")?.focus();
        return;
    }
    if (!fechaInput.checkValidity()) {
        showNotice("La fecha y hora deben ser actuales o futuras.", true);
        fechaInput.focus();
        return;
    }
    if (!Number.isInteger(minimo) || !Number.isInteger(maximo) || minimo < 1 || maximo < 1) {
        showNotice("Los cupos deben ser números enteros mayores que cero.", true);
        createForm.elements.cupoMinimo.focus();
        return;
    }
    if (minimo > maximo) {
        showNotice("El cupo mínimo no puede superar al máximo.", true);
        return;
    }

    const payload = {
        deporte,
        fechaHora,
        cancha,
        cupoMinimo: minimo,
        cupoMaximo: maximo,
        organizador
    };

    setSubmitting(createForm, true, "Crear encuentro →");
    try {
        const encounter = await requestEncounter(apiUrl, {
            method: "POST",
            body: JSON.stringify(payload)
        });
        createForm.reset();
        lookupId.value = encounter.id;
        lookupSequence++;
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
    const requestSequence = ++lookupSequence;
    const id = lookupId.value.trim();
    if (!id || !Number.isSafeInteger(Number(id)) || Number(id) < 1) {
        showNotice("Escribe un ID entero mayor que cero.", true);
        lookupId.focus();
        return;
    }

    encounterCard.hidden = true;
    emptyState.hidden = false;
    document.querySelector("#estado").textContent = "";
    actionButtons.replaceChildren();
    actionNotice.hidden = true;
    actionNotice.textContent = "";
    participantForm.hidden = true;
    emptyState.querySelector("p").textContent = "Buscando encuentro…";
    emptyState.querySelector("span:last-child").textContent = "Consultando el ID en SportsMatch.";
    setSubmitting(lookupForm, true, "Buscar");
    try {
        const encounter = await requestEncounter(`${apiUrl}/${encodeURIComponent(id)}`);
        if (requestSequence !== lookupSequence) return;
        renderEncounter(encounter);
        showNotice(`Encuentro ${encounter.id} cargado.`);
    } catch (error) {
        if (requestSequence !== lookupSequence) return;
        emptyState.querySelector("p").textContent = "No se pudo cargar el encuentro.";
        emptyState.querySelector("span:last-child").textContent = "Revisa el ID e inténtalo otra vez.";
        showNotice(error.message, true);
    } finally {
        if (requestSequence === lookupSequence) setSubmitting(lookupForm, false, "Buscar");
    }
});

actionButtons.addEventListener("click", async (event) => {
    const button = event.target.closest("button[data-action]");
    if (!button) return;

    actionNotice.hidden = true;
    actionNotice.textContent = "";
    const id = document.querySelector("#detalleId").textContent;
    const requestSequence = lookupSequence;
    button.disabled = true;
    try {
        const encounter = await requestEncounter(`${apiUrl}/${id}/${button.dataset.action}`, { method: "POST" });
        if (requestSequence !== lookupSequence) return;
        renderEncounter(encounter);
        showActionNotice(`Estado actualizado: ${encounter.estado.replaceAll("_", " ")}.`);
    } catch (error) {
        showActionNotice(error.message, true);
        button.disabled = false;
    }
});

participantForm.addEventListener("submit", async (event) => {
    event.preventDefault();
    actionNotice.hidden = true;
    actionNotice.textContent = "";
    const button = participantForm.querySelector("button[type='submit']");
    button.disabled = true;
    try {
        const participant = new FormData(participantForm).get("participante").trim();
        const id = document.querySelector("#detalleId").textContent;
        const requestSequence = lookupSequence;
        const encounter = await requestEncounter(`${apiUrl}/${id}/participantes`, {
            method: "POST",
            body: JSON.stringify({ participante: participant })
        });
        if (requestSequence !== lookupSequence) return;
        participantForm.reset();
        renderEncounter(encounter);
        showActionNotice(`${participant} ya está inscrito.`);
    } catch (error) {
        showActionNotice(error.message, true);
    } finally {
        button.disabled = false;
    }
});

const fechaInput = document.querySelector("#fechaHora");
const ahora = new Date();
ahora.setMinutes(ahora.getMinutes() - ahora.getTimezoneOffset());
fechaInput.min = ahora.toISOString().slice(0, 16);

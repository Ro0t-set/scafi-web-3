import './style.css';
import 'scalajs:main.js';

import { addNodesFromJson } from 'scalajs:main.js';

let animationFrameId = null; // ID per requestAnimationFrame
let engine = null; // Riferimento al motore

// Funzione per avviare il programma dopo il caricamento dello script
function startProgramAfterScriptLoad() {
    // Cancella eventuali cicli esistenti
    if (animationFrameId !== null) {
        cancelAnimationFrame(animationFrameId); // Interrompi il ciclo precedente
        animationFrameId = null;
    }

    // Crea un nuovo motore
    engine = new EngineImpl(10, 10, 2, 100, 100, 100, 180);

    async function loop() {
        if (!engine) return; // Interrompi se l'engine è stato resettato
        let data = await engine.nextAndGetJsonNetwork();
        data = data.ju_concurrent_atomic_AtomicReference__f_value.s_util_Success__f_value;
        addNodesFromJson(data);
        animationFrameId = window.requestAnimationFrame(loop); // Continua il ciclo
    }

    animationFrameId = window.requestAnimationFrame(loop); // Avvia il ciclo
}

// Wrapping della funzione scastie.ClientMain.signal
const originalSignal = scastie.ClientMain.signal;
scastie.ClientMain.signal = function (result, attachedElements, scastieId) {
    console.log("Signal intercepted!", { result, attachedElements, scastieId });

    // Interrompi il motore corrente e avvia un nuovo ciclo
    engine = null; // Resetta il motore esistente
    startProgramAfterScriptLoad(); // Avvia un nuovo ciclo

    originalSignal.apply(this, arguments); // Chiamata originale
};



import './style.css';
import 'scalajs:main.js';

import { addNodesFromJson } from 'scalajs:main.js';

let animationFrameId = null;
let engine = null;

function startProgramAfterScriptLoad() {
    if (animationFrameId !== null) {
        cancelAnimationFrame(animationFrameId); // Interrompi il ciclo precedente
        animationFrameId = null;
    }

    engine = new EngineImpl(10, 10, 2, 100, 100, 100, 180);

    async function loop() {
        if (!engine) return;
        let data = await engine.nextAndGetJsonNetwork();
        data = data.ju_concurrent_atomic_AtomicReference__f_value.s_util_Success__f_value;
        addNodesFromJson(data);
        animationFrameId = window.requestAnimationFrame(loop);
    }

    animationFrameId = window.requestAnimationFrame(loop);
}


const originalSignal = scastie.ClientMain.signal;
scastie.ClientMain.signal = function (result, attachedElements, scastieId) {
    console.log("Signal intercepted!", { result, attachedElements, scastieId });
    engine = null;
    startProgramAfterScriptLoad();
    originalSignal.apply(this, arguments);
};



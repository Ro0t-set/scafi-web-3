import './style.css';
import 'scalajs:main.js';

import { addNodesFromJson } from 'scalajs:main.js';
import {EngineImpl} from './customProgram.js';

const engine =  EngineImpl(10,10,2,100,100,100,180);
let data = engine.nextAndGetJsonNetwork();
async function loop() {
    data = await engine.nextAndGetJsonNetwork();
    data = data.ju_concurrent_atomic_AtomicReference__f_value.s_util_Success__f_value
    addNodesFromJson(data);
    window.requestAnimationFrame(loop);
}

window.requestAnimationFrame(loop);

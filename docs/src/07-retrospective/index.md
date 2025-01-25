# Retroscena

In questa sezione verranno spiegati alcuni problemi che sono emersi durante lo sviluppo del progetto e come sono stati risolti. Purtroppo essendo un progetto che usa tecnologie di nicchia è complesso trovare soluzioni già pronte e spesso mi sono confrontato con la comunita Scala.js per trovare soluzioni a problemi che non avevo mai affrontato prima. Essendo anche stato un lavoro in solitario, questo approccio è stato molto utile per avere un confronto con altre persone. In particolare vorrei ringraziare [Nikita Gazarov](https://github.com/raquo), sviluppatore di Laminar, per avermi aiutato a risolvere un problema di memory leak che stava affliggendo il progetto.

## L'idea iniziale

L'apporccio iniziala ap problema della compilazione scastie è stato quello di unire i sui file javascript dopo la compilaizone tramite scala js. Questo approccio è passialmente possibile a patto le funzioni interscambiabili siano dei js.Object. Questo approccio quindi porta alla perdita di tipizazione, e ad una flessibilità molto bassa, quindi si è optato per l'ulilizzo di due sorgenti differenti che comunicano tramite Json.

## Memory leak

### L'eliminazione di oggetti 3D in Three.js

È importante sapere che usando la funzione remove di Three.js non elimina l'oggetto dalla memoria, ma solo dallo schermo. Per eliminare l'oggetto dalla memoria è necessario chiamare la funzione dispose. Questo è stato un problema che ha afflitto il progetto per molto tempo, e degradava le prestazioni del progetto.

### Programmazione Reattiva ed Ownership

 È importante notare che la gestione dell'ownership in Airstream non è sempre automatica. In alcuni casi, è necessario intervenire manualmente per garantire che gli Observable non necessari vengano disattivati e resi disponibili per il garbage collection. Ad esempio, se un Observable è associato a un componente UI che viene distrutto, ma l'Observable stesso non è gestito da un Owner appropriato, potrebbe continuare a funzionare e consumare risorse, causando un memory leak. Pertanto, è fondamentale assicurarsi che tutti gli Observable siano correttamente associati a un Owner e, se necessario, gestire manualmente la loro disattivazione per prevenire problemi di memoria [Owner Airstream](https://arc.net/l/quote/emkirpgc).

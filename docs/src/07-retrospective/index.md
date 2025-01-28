# Retroscena  

In questa sezione verranno descritti i problemi emersi durante lo sviluppo del progetto e le soluzioni adottate. L’utilizzo di tecnologie di nicchia ha reso complesso trovare soluzioni già pronte. Per questo motivo, il confronto con la comunità Scala.js è stato fondamentale per affrontare problematiche inedite. Essendo un progetto sviluppato in solitaria, il supporto della comunità è stato prezioso per condividere idee e trovare soluzioni. In particolare, desidero ringraziare [Nikita Gazarov](https://github.com/raquo), sviluppatore di Laminar, per l’aiuto nella risoluzione di un problema di memory leak che aveva un impatto significativo sulle prestazioni del progetto.  

## L’idea iniziale  

L’approccio iniziale al problema della compilazione di Scastie prevedeva l’unione dei file JavaScript generati dopo la compilazione. Questa soluzione era tecnicamente possibile solo se le funzioni interscambiabili fossero istanze di `js.Object`. Tuttavia, tale approccio comportava la perdita della tipizzazione e una scarsa flessibilità. Per superare queste limitazioni, si è scelto di adottare due sorgenti distinte che comunicano tramite JSON.  

## Memory Leak  

### Eliminazione di oggetti 3D in Three.js  

È importante notare che la funzione `remove` di Three.js non elimina gli oggetti dalla memoria, ma soltanto dal rendering a schermo. Per rimuovere un oggetto dalla memoria è necessario utilizzare il metodo `dispose`. Questo problema ha afflitto il progetto per lungo tempo, causando un progressivo degrado delle prestazioni.  

### Programmazione Reattiva e Ownership  

La gestione dell’ownership in Airstream non è sempre automatica. In alcuni casi, è necessario intervenire manualmente per garantire che gli `Observable` non più necessari vengano disattivati e resi disponibili per il garbage collection. Ad esempio, se un `Observable` è associato a un componente UI che viene distrutto, ma non è gestito da un `Owner` appropriato, potrebbe continuare a funzionare, consumando risorse inutilmente e causando un memory leak. È quindi essenziale assicurarsi che tutti gli `Observable` siano correttamente associati a un `Owner`. In caso contrario, è necessario disattivarli manualmente per prevenire problemi di memoria. Per approfondire, si rimanda alla documentazione di [Owner Airstream](https://arc.net/l/quote/emkirpgc).  


# Requisiti

## Funzionali

1. Utilizzo di *Scastie* per la compilazione del programma aggregato.
2. Possibilità di caricare codice direttamente dal proprio account *Scastie*.
3. Visualizzatore 3D del grafo.
4. Player per la gestione del grafo con funzioni di *Play*, *Pausa* e regolazione della velocità.
5. Possibilità di modificare le dimensioni del grafo.
6. Inclusione di vari esempi di programmi aggregati (da aggiungere).
7. Utilizzo di *Cucumber* per i test, integrato con *Selenium* per testare la grafica.
8. CI/CD per test e deploy.
9. Il grafo supporta più di 30 aggiornamenti al secondo.

## Non funzionali
1. Affidabilità: l'applicazione deve essere stabile, evitando crash.
2. Documentazione: l'intero progetto deve essere ben documentato, in modo da facilitare la comprensione del codice.
3. Performance: l'applicazione deve essere veloce e reattiva, evitando prolungate attese per l'utente nello svolgimento delle azioni.
4. Portabilità: l'applicazione deve essere disponibile su più piattaforme.
5. Manutenibilità: il codice deve essere ben strutturato e facilmente manutenibile.


## Requisiti Opzionali
1. Possibiltià di centrare il grafo.
2. Possibilità di cambiare la grafica del grafo.
3. Nascondere il boilerplate Scastie.
4. Cambiare la visualizazione da 3D a 2D.

## Implementazione

Utilizzo di:

- Scala 3.3.4
- ScalaJS 1.18.x
- Laminar 1.17.x
- Cucumber 8.25.x
- MUnit 1.0.x
- JDK 21+
- Scastie (embedded)
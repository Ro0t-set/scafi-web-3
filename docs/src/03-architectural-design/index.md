# Architettura

## Livelli

- **Domain**: Contiene la logica di business e i concetti fondamentali (core model) del sistema. È la parte “pura” dell’applicazione, priva di dipendenze da framework o librerie specifiche.

- **State**: Si occupa di gestire lo stato dell’applicazione e le sue transizioni. Contiene il meccanismo che tiene traccia delle modifiche al dominio ed espone operazioni per aggiornare lo stato.

- **View**: Si basa su un design a componenti, ovvero su piccoli moduli (UI Components) che si occupano ciascuno di una parte di interfaccia. Gestisce la presentazione dei dati e l’interazione con l’utente.

- **Graph Infrastructure**: Rappresenta il motore per il rendering e l’integrazione con librerie esterne (adattate tramite l’Adapter Pattern). Ciò permette di mantenere disaccoppiati i livelli più alti (Domain/State/View) dalla logica specifica di rendering.

```mermaid
    flowchart TD
        A[Domain] --> B[State]
        B --> C[View]
        C --> D[Graph Infrastructure]
        E[Scastie] -->|API| B[State]
```

## Pattenr Architetturali

- **Component-Based Design**: La View è organizzata in componenti riutilizzabili, ognuno responsabile di un aspetto specifico dell’interfaccia. Questo semplifica la manutenzione e promuove la riusabilità.

- **Adapter Pattern**: Utilizzato principalmente nella Graph Infrastructure per adattare librerie o API esterne al modello e alle interfacce dell’applicazione, mantenendo l’applicazione (Domain/State/View) indipendente dai dettagli di implementazione.

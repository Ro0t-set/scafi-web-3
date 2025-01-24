# Processo di Sviluppo

## Messaggi di Commit

Per la scrittura dei messaggi di commit, è stata adottata la convenzione [Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0/), che fornisce uno standard chiaro e coerente per descrivere le modifiche apportate al progetto. È stato inoltre utilizzato _Open Commit_ per generare automaticamente i messaggi di commit.

## Testing

Per garantire la correttezza delle funzionalità sviluppate, è stato adottato l'approccio _Behavior Driven Development_ (BDD) utilizzando [_Cucumber_](https://cucumber.io/).

I test vengono eseguiti tramite _Cucumber_ integrato con [_Selenium_](https://www.selenium.dev/) per la simulazione di interazioni utente e test di interfaccia.

## Build

Come tool di build è stato scelto _sbt_, che gestisce le dipendenze del progetto e facilita l'esecuzione dei test e la generazione della documentazione.

## Qualità del Codice

Per mantenere elevati standard di qualità del codice, sono stati adottati i seguenti strumenti:

- **Scalafmt**: per la formattazione automatica del codice.
- **Scalafix**: per il refactoring e il miglioramento della qualità del codice.
- **sbt-wartremover**: per l'analisi statica del codice e la rilevazione di possibili errori.

## CI/CD

Per l'integrazione e il deployment continui (_Continuous Integration_ e _Continuous Deployment_), è stato scelto di utilizzare GitHub Actions. Sono stati definiti i seguenti workflow:

- **Pages Deploy**: Generazione dell'artefatto web e caricamento su GitHub Pages, comprendendo sia il progetto che la documentazione.
- **Test & Format**: Esecuzione automatica dei test e verifica del formato del codice.

In caso di fallimento di un workflow, il membro del team che ha avviato l'esecuzione viene notificato via email per consentire un intervento tempestivo.

## Documentazione

La documentazione del progetto è redatta utilizzando il linguaggio Markdown e mantenuta in un branch dedicato. Successivamente, viene pubblicata su GitHub Pages per garantirne la consultazione online.

Per la generazione della documentazione in formato HTML è stato utilizzato il tool [vitepress](https://vitepress.vuejs.org/) in maniera da integrare la documentazione con il progetto che gia utilizza _Vite_.

::: warning
Al momento dell'esame non é stato possibile generare la scala doc: [GitHub Issue](https://github.com/scala/scala3/issues/22447)
:::

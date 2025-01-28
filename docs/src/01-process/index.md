# Processo di Sviluppo

## Messaggi di Commit

Per la scrittura dei messaggi di commit, è stata adottata la convenzione [Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0/), che fornisce uno standard chiaro e coerente per descrivere le modifiche apportate al progetto. È stato inoltre utilizzato [_Open Commit_](https://github.com/marketplace/actions/opencommit-improve-commits-with-ai) per generare automaticamente i messaggi di commit.

## Testing

Per garantire la correttezza delle funzionalità sviluppate, è stato adottato l'approccio _Behavior Driven Development_ (BDD) utilizzando [_Cucumber_](https://cucumber.io/). I test vengono eseguiti tramite _Cucumber_ integrato con [_Selenium_](https://www.selenium.dev/) per la simulazione di interazioni utente e test di interfaccia. Approfondimento nel [capitolo dedicato](../06-testing/index.md).

## Build

Come tool di build è stato scelto _sbt_, che gestisce le dipendenze del progetto e facilita l'esecuzione dei test e la generazione della documentazione.

## Qualità del Codice

Per mantenere elevati standard di qualità del codice, sono stati adottati i seguenti strumenti:

- [**Scalafmt**](https://scalameta.org/scalafmt/): per la formattazione automatica del codice.
- [**Scalafix**](https://scalacenter.github.io/scalafix/): per il refactoring e il miglioramento della qualità del codice.
- [**Wartremover**](https://www.wartremover.org/): per l'analisi statica del codice e la rilevazione di possibili errori.

## CI/CD

Per _Continuous Integration_ e _Continuous Deployment_, è stato scelto di utilizzare le GitHub Actions. Sono stati definiti i seguenti workflow:

- **Pages Deploy**: Generazione dell'artefatto web e caricamento su GitHub Pages, comprendendo sia il progetto che la documentazione.
- **Test & Format**: Esecuzione automatica dei test e verifica del formato del codice.

## Documentazione

La documentazione del progetto è redatta utilizzando il linguaggio Markdown. Viene pubblicata su GitHub Pages per garantirne la consultazione online.

Per la generazione della documentazione in formato HTML è stato utilizzato il tool [vitepress](https://vitepress.vuejs.org/) in maniera da integrare la documentazione con il progetto che già utilizza _Vite_.

::: warning
Al momento dell'esame non è stato possibile generare la scala doc: [GitHub Issue](https://github.com/scala/scala3/issues/22447)
:::

# Scafi Web 3D

![logo](docs/src/img/logo.png)



[![Build Status](https://img.shields.io/github/actions/workflow/status/Ro0t-set/PPS-24-ScafiWeb3/pages.yml?branch=dev)](https://github.com/Ro0t-set/PPS-24-ScafiWeb3/actions/workflows/pages.yml)
---

**Access the official documentation**:  
👉 [Scafi Web 3D Docs](https://www.tommasopatriti.me/PPS-24-ScafiWeb3/) 👈

---

## How to Run the Project

```bash
git clone https://github.com/Ro0t-set/PPS-24-ScafiWeb3.git
cd PPS-24-ScafiWeb3
cd js && npm install && cd ..
npm install
sbt fastLinkJS
npm run dev
```

---

## sbt Tests

- `sbt test`
- `sbt cucumber -DtestEnv=<local, ci> -Dbrowser=<edge, firefox, chrome>`  
  *(Default: `ci` and `firefox`)*
- `sbt cucumberWithServer` *(Uses `ci` and `firefox` by default)*

---

## npm Commands

- `npm install` — Install dependencies.
- `npm run dev` — Run the project in development mode.
- `npm run build` — Build the production version of the project.
- `npm run docs:dev` — Serve the documentation in development mode.
- `npm run docs:build` — Build the documentation for production.

---

## Example Screenshots

- **Main Interface**  
  ![Scafi3 screen](docs/src/img/screen.jpeg)

- **Gradient Effect**  
  ![Gradient Example](docs/src/img/gradient.png)

- **Sphere Visualization**  
  ![Sphere Example](docs/src/img/sphere.png)


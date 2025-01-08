import { defineConfig } from "vite";
import scalaJSPlugin from "@scala-js/vite-plugin-scalajs";

export default defineConfig(({ mode }) => {
  return {
    plugins: [scalaJSPlugin()],
    base: mode === "production" ? "./" : "/",
    server: {
      port: 8080
    },
    build: {
      outDir: "dist",
    },
  };
});

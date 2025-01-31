import { defineConfig } from "vite";
import scalaJSPlugin from "@scala-js/vite-plugin-scalajs";

export default defineConfig(({ mode }) => {
  return {
    plugins: [
      scalaJSPlugin({
        projectID: 'scafiWeb3', // Match sbt project name
        sbtTask: 'fastLinkJS',  // Use correct task name
        outputDir: './js/target/scala-3.3.4/scafiweb3-fastopt'
      })
    ],
    base: mode === "production" ? "./" : "/",
    server: {
      port: 8080
    },
    build: {
      outDir: "dist",
    },
  };
});

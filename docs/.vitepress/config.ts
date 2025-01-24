import { defineConfig } from 'vitepress'
import { withMermaid } from "vitepress-plugin-mermaid";
// https://vitepress.dev/reference/site-config
export default withMermaid({
  base: '/PPS-25-ScafiWeb3/',
  title: "ScafiWeb3",
  description: "ScafiWeb3 doc",
  themeConfig: {
    // https://vitepress.dev/reference/default-theme-config
    nav: [
      { text: 'Docs', link: 'src/introduction.md' },
      { text: 'GitHub', link: 'https://github.com/Ro0t-set/PPS-25-ScafiWeb3' },
      { text: 'Cucumber', link: 'cucumber/' },
      { text: 'Demo', link: 'dist/' }
    ],

    sidebar: [
      {
        text: 'Documentazione',
        items: [
          { text: 'Introduzione', link: 'src/introduction.md' },
          { text: 'Processo di sviluppo', link: 'src/01-process/index.md' },
          { text: 'Requisiti', link: 'src/02-requirements/index.md' },
          { text: 'Architettura', link: 'src/03-architectural-design/index.md' },
          { text: 'Design', link: 'src/04-design/index.md' },
          { text: 'Implementazione', link: 'src/05-implementation/index.md' }
        ]
      }
    ],


    socialLinks: [
      { icon: 'github', link: 'https://github.com/Ro0t-set' }
    ]
  }

})

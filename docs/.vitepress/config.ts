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
      { text: 'Introduzione', link: '/PPS-25-ScafiWeb3/introduction.md' }
    ],

    sidebar: [
      {
        text: 'Documentazione',
        items: [
          { text: 'Introduzione', link: '/PPS-25-ScafiWeb3/src/introduction.md' },
          { text: 'Processo di sviluppo', link: '/PPS-25-ScafiWeb3/src/01-process/index.md' },
          { text: 'Requisiti', link: '/PPS-25-ScafiWeb3/src/02-requirements/index.md' },
          { text: 'Architettura', link: '/PPS-25-ScafiWeb3/src/03-architectural-design/index.md' },
          { text: 'Design', link: '/PPS-25-ScafiWeb3/src/04-design/index.md' },
          { text: 'Implementazione', link: '/PPS-25-ScafiWeb3/src/05-implementation/index.md' }
        ]
      }
    ],


    socialLinks: [
      { icon: 'github', link: 'https://github.com/Ro0t-set' }
    ]
  }

})

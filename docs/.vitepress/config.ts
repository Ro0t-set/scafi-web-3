import { defineConfig } from 'vitepress'
import { withMermaid } from "vitepress-plugin-mermaid";

// https://vitepress.dev/reference/site-config
export default withMermaid(
  defineConfig({
    base: '/PPS-25-ScafiWeb3/',
    title: "ScafiWeb3",
    description: "ScafiWeb3 doc",
    themeConfig: {
      // https://vitepress.dev/reference/default-theme-config
      nav: [
        { text: 'Docs', link: '/src/introduction' }, // Fixed relative link
        { text: 'GitHub', link: 'https://github.com/Ro0t-set/PPS-25-ScafiWeb3' },
        { text: 'Cucumber', link: '/cucumber/index.html', target: '_blank', rel: 'noopener noreferrer' },
        { text: 'Demo', link: '/dist/index.html', target: '_blank', rel: 'noopener noreferrer' }
      ],

      sidebar: [
        {
          text: 'Documentazione',
          items: [
            { text: 'Introduzione', link: '/src/introduction' },
            { text: 'Processo di sviluppo', link: '/src/01-process/' },
            { text: 'Requisiti', link: '/src/02-requirements/' },
            { text: 'Architettura', link: '/src/03-architectural-design/' },
            { text: 'Design', link: '/src/04-design/' },
            { text: 'Implementazione', link: '/src/05-implementation/' }
          ]
        }
      ],

      socialLinks: [
        { icon: 'github', link: 'https://github.com/Ro0t-set' }
      ]
    }
  })
);

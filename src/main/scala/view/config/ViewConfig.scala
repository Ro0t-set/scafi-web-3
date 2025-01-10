package view.config

import view.graph.config.SceneConfig

final case class ViewConfig(
    sceneConfig: SceneConfig = SceneConfig(
      width = 600,
      height = 550
    )
)

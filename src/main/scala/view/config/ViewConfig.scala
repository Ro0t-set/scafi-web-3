package view.config
import view.graph.SceneConfig

final case class ViewConfig(
    sceneConfig: SceneConfig = SceneConfig(
      width = 600,
      height = 550,
      zPointOfView = 1000
    )
)

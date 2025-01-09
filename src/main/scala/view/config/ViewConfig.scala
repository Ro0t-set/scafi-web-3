package view.config
import view.graph.SceneConfig

final case class ViewConfig(
    windowsWidth: Int = 700,
    windowsHeight: Int = 400,
    sceneConfig: SceneConfig = SceneConfig(
      width = 600,
      height = 600,
      zPointOfView = 1000
    )
)

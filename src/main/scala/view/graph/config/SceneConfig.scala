package view.graph.config

case class SceneConfig(
    width: Int,
    height: Int,
    fov: Int = 65,
    near: Double = 0.1,
    far: Double = 1600
)

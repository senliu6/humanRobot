//package com.shciri.rosapp.ui
//
//import android.os.Bundle
//import android.view.Choreographer
//import android.view.SurfaceHolder
//import android.view.SurfaceView
//import androidx.appcompat.app.AppCompatActivity
//import com.google.android.filament.Camera
//import com.google.android.filament.Engine
//import com.google.android.filament.EntityManager
//import com.google.android.filament.Renderer
//import com.google.android.filament.Scene
//import com.google.android.filament.SwapChain
//import com.google.android.filament.View
//import com.google.android.filament.Viewport
//import com.google.android.filament.gltfio.AssetLoader
//import com.google.android.filament.gltfio.FilamentAsset
//import com.google.android.filament.gltfio.ResourceLoader
//import com.google.android.filament.gltfio.UbershaderProvider
//import java.nio.ByteBuffer
//
//class MainTestActivity : AppCompatActivity() {
//
//    private lateinit var filamentEngine: Engine
//    private lateinit var renderer: Renderer
//    private lateinit var scene: Scene
//    private lateinit var view: View
//    private lateinit var swapChain: SwapChain
//    private lateinit var camera: Camera
//
//    private var assetLoader: AssetLoader? = null
//    private var resourceLoader: ResourceLoader? = null
//    private var modelAsset: FilamentAsset? = null
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(com.shciri.rosapp.R.layout.test)
//
//        // 初始化 Filament 引擎
//        filamentEngine = Engine.create()
//        renderer = filamentEngine.createRenderer()
//        scene = filamentEngine.createScene()
//        view = filamentEngine.createView()
//        view.scene = scene
//
//        // 配置摄像机
//        camera = filamentEngine.createCamera(EntityManager.get().create())
//        view.camera = camera
//        view.viewport = Viewport(0, 0, resources.displayMetrics.widthPixels, resources.displayMetrics.heightPixels)
//
//        val surfaceView = findViewById<SurfaceView>(com.shciri.rosapp.R.id.surfaceView)
//        surfaceView.holder.addCallback(object : SurfaceHolder.Callback {
//            override fun surfaceCreated(holder: SurfaceHolder) {
//                swapChain = filamentEngine.createSwapChain(holder.surface)
//            }
//
//            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
//                view.viewport = Viewport(0, 0, width, height)
//                camera.setProjection(45.0, width.toDouble() / height, 0.1, 10.0, Camera.Fov.VERTICAL)
//            }
//
//            override fun surfaceDestroyed(holder: SurfaceHolder) {
//                filamentEngine.destroySwapChain(swapChain)
//            }
//        })
//
//        setupModel()
//    }
//
//    private fun setupModel() {
//        val entityManager = EntityManager.get()
//        assetLoader = AssetLoader(filamentEngine, UbershaderProvider(filamentEngine), entityManager)
//        resourceLoader = ResourceLoader(filamentEngine)
//
//        val modelInputStream = assets.open("scene.gltf")
//        val modelData = ByteBuffer.allocateDirect(modelInputStream.available())
//        modelInputStream.read(modelData.array())
//        modelInputStream.close()
//
//        // 从二进制数据创建资产
//        modelAsset = assetLoader?.createAsset(modelData)
//        modelAsset?.let { asset ->
//            resourceLoader?.loadResources(asset)
//            scene.addEntities(asset.entities)
//        }
//    }
//
//    override fun onResume() {
//        super.onResume()
//        Choreographer.getInstance().postFrameCallback(frameCallback)
//    }
//
//    override fun onPause() {
//        super.onPause()
//        Choreographer.getInstance().removeFrameCallback(frameCallback)
//    }
//
//    private val frameCallback = object : Choreographer.FrameCallback {
//        override fun doFrame(frameTimeNanos: Long) {
////            if (renderer.beginFrame(swapChain)) {
////                renderer.render(view)
////                renderer.endFrame()
////            }
//            renderer.render(view)
//            renderer.endFrame()
//            // 直接调用 this 避免重复传递 frameTimeNanos
//            Choreographer.getInstance().postFrameCallback(this)
//        }
//    }
//
//
//    override fun onDestroy() {
//        super.onDestroy()
//        modelAsset?.let { assetLoader?.destroyAsset(it) }
//        resourceLoader?.destroy()
//        filamentEngine.destroy()
//    }
//}

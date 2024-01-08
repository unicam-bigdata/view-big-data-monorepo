import './App.css';
import { TextureLoader, SpriteMaterial, Sprite } from 'three'
import { useMemo, useEffect } from 'react'
import { OrbitControls } from '@react-three/drei'
import { useFrame, useThree } from '@react-three/fiber'
import ThreeForceGraph from 'three-forcegraph'
import { gData } from './graph.js'

const loader = new TextureLoader();

const imgToNode = ({ img }) => {
  const imgTexture = loader.load('/node.png')
  const material = new SpriteMaterial({ map: imgTexture })
  const sprite = new Sprite(material)
  sprite.scale.set(50, 50)
  
  return sprite
}

export default function App() {
  const { scene } = useThree()

  const graph = useMemo(() => { 
    return new ThreeForceGraph().graphData(gData).nodeThreeObject(imgToNode) 
  }, [])

  useEffect(() => {
    while (scene.children.length > 0) {
      scene.remove(scene.children[0])
    }

    graph.linkWidth(3)
    graph.linkOpacity(1)

    scene.add(graph)
  }, [])

  useFrame(() => {
    graph.tickFrame()
  })

  return (
    <>
      <OrbitControls />
    </>
  )
}
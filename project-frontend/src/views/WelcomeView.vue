<script setup>

</script>

<template>
  <div style="width: 100vw; height: 100vh; overflow: hidden; display: flex">
    <div style="flex: 1; background-color: aquamarine">
      <el-image style="width: 100%;height: 100%;" fit="cover"
                src="https://c.wallhere.com/photos/98/e8/1697x981_px_Artifacts_black_Brown_clothes_eyes_Ghostblade_hair-1855075.jpg!d"/>
    </div>
    <div style="width: 400px; background-color: white">
      <router-view v-slot="{ Component }">
        <transition name="el-fade-in-linear" mode="out-in">
          <component :is="Component" style="height: 100%" />
        </transition>
      </router-view>
    </div>
  </div>
</template>

<style scoped>

</style>
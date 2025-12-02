<script setup lang="ts">
import NavBar from './NavBar.vue'
import FootBar from './FootBar.vue'
import { Toaster } from 'vue-sonner'
</script>

<template>
  <div class="flex flex-col min-h-screen">
    <NavBar />
    <RouterView v-slot="{ Component, route }">
      <transition name="fade" mode="out-in">
        <main :key="route.path" class="py-8">
          <component :is="Component" />
        </main>
      </transition>
    </RouterView>
    <Toaster position="bottom-right" theme="light" rich-colors />
    <FootBar />
  </div>
</template>

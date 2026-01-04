<script setup lang="ts">
import { TabGroup, TabList, Tab, TabPanels, TabPanel } from '@headlessui/vue'
import useCurrentUser from '@/queries/useCurrentUser'
import { useInvalidateCurrentUserQuery } from '@/mutations/user'
import SettingsInformation from '@/components/settings/SettingsInformation.vue'
import SettingsPassword from '@/components/settings/SettingsPassword.vue'
import SettingsAvatar from '@/components/settings/SettingsAvatar.vue'
import SettingsPrivacy from '@/components/settings/SettingsPrivacy.vue'

const { data: user, isStale } = useCurrentUser()

// Invalidate profile cache when entering this page
const invalidateCurrentUser = useInvalidateCurrentUserQuery()
if (user && !isStale) invalidateCurrentUser.mutate()
</script>

<template>
  <div class="grow flex flex-col items-center justify-center text-center px-4 py-8">
    <div class="max-w-4xl w-full bg-white p-6 rounded shadow-md text-left">
      <h1 class="text-4xl font-bold mb-6 text-center">Ustawienia konta</h1>
      <TabGroup>
        <TabList class="flex space-x-1 border-b border-gray-200 mb-6">
          <Tab
            class="px-4 py-2 text-sm font-medium rounded-t transition-colors ui-selected:bg-gray-200 ui-selected:text-gray-900 text-gray-700 hover:bg-gray-100"
          >
            Informacje
          </Tab>
          <Tab
            class="px-4 py-2 text-sm font-medium rounded-t transition-colors ui-selected:bg-gray-200 ui-selected:text-gray-900 text-gray-700 hover:bg-gray-100"
          >
            Hasło
          </Tab>
          <Tab
            class="px-4 py-2 text-sm font-medium rounded-t transition-colors ui-selected:bg-gray-200 ui-selected:text-gray-900 text-gray-700 hover:bg-gray-100"
          >
            Awatar
          </Tab>
          <Tab
            class="px-4 py-2 text-sm font-medium rounded-t transition-colors ui-selected:bg-gray-200 ui-selected:text-gray-900 text-gray-700 hover:bg-gray-100"
          >
            Prywatność
          </Tab>
        </TabList>
        <TabPanels>
          <TabPanel>
            <SettingsInformation />
          </TabPanel>
          <TabPanel>
            <SettingsPassword />
          </TabPanel>
          <TabPanel>
            <SettingsAvatar />
          </TabPanel>
          <TabPanel>
            <SettingsPrivacy />
          </TabPanel>
        </TabPanels>
      </TabGroup>
    </div>
  </div>
</template>

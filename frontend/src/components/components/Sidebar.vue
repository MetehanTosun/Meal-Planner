<template>
    <div class="sidebar">
        <div class="sidebar-header">Rezepte</div>
        <SearchbarComponent @update:filtered-recipes="$emit('update:filtered-recipes', $event)" />
        <ul class="recipe-list">
            <li v-for="(item, index) in filteredRecipes" :key="index">
                {{ item.name }}
            </li>
        </ul>
    </div>
</template>

<script>
import SearchbarComponent from './SearchbarComponent.vue';

export default {
    name: 'Sidebar',
    components: {
        SearchbarComponent,
    },
    props: {
        filteredRecipes: {
            type: Array,
            required: true,
        },
    },
    emits: ['update:filtered-recipes'],
};
</script>

<style scoped>
.sidebar {
    width: 250px;
    background-color: #ddd;
    padding: 10px;
    box-sizing: border-box;
}

.sidebar-header {
    font-weight: bold;
    margin-bottom: 10px;
    background-color: #888;
    padding: 5px;
    color: white;
}

.recipe-list {
    list-style-type: none;
    padding: 0;
    margin: 0;
}

.recipe-list li {
    margin: 10px 0;
    cursor: pointer;
}
</style>
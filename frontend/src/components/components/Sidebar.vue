<template>
    <div class="sidebar">
        <div class="sidebar-header"><p>Recipes</p></div>
        <SearchbarComponent @update:filtered-recipes="$emit('update:filtered-recipes', $event)" />
        <ul class="recipe-list">
            <li v-for="(item, index) in filteredRecipes" :key="index" :draggable=true @dragstart="dragStart($event, item)"
                @dragend="dragEnd()">
                <p>{{ item.name }}</p>
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
    data() {
        return {
            draggedItem: null
        };
    },
    methods: {
        dragStart(event, item) {
            if (this.draggedItem == null) {
                this.draggedItem = item;
                
                event.dataTransfer.setData("application/json", JSON.stringify(item))
                console.log("Currently Dragged Item:" + JSON.stringify(item, null, 2));
            }
        },
        dragEnd() {
            this.draggedItem = null;
        }
    }
};
</script>

<style scoped>
.sidebar {
    width: 250px;
    background-color: rgba(25, 30, 40, 0.95);
    padding: 20px;
    box-sizing: border-box;
    border-radius: 8px;
    box-shadow: 0px 4px 12px rgba(0, 0, 0, 0.2); 
}

.sidebar-header {
    font-weight: bold;
    font-size: 18px;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 10px;
    background-color: rgba(70, 130, 180, 0.9);
    color: white;
    border-radius: 6px;
    margin-bottom: 15px;
    text-transform: uppercase;
}

.recipe-list {
    list-style-type: none;
    padding: 0;
    margin: 0;
}

.recipe-list li {
    margin: 10px 0;
    padding: 8px 12px;
    cursor: pointer;
    color: rgba(200, 200, 200, 1);
    background-color: rgba(40, 45, 55, 0.8);
    border-radius: 6px;
    text-align: center;
    transition: background-color 0.3s ease, transform 0.2s ease;
}
.recipe-list li:hover {
    background-color: rgba(70, 130, 180, 0.3);
    transform: scale(1.02);
}
</style>
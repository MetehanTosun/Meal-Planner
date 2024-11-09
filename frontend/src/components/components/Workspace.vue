<template>
    <div class="day-container">
        <div class="menu-bar">
            <div class="menu-item" v-for="day in 7" 
            :key="day"
            :dropzone=true>
                <p class="item-label">Day {{ day }}</p>
                <div class="drop-area"></div>
            </div>
        </div>
    </div>
</template>

<script>
export default {};
</script>

<style>

.day-container {
    padding: 20px 20px 20px 20px;
    background-color: #181818;
    display: flex;
    justify-content: center;
    align-items: flex-start;
    height: 100vh;
    width: 100vw;
}

.menu-bar {
    display: flex;
    width: 100%;
    gap: 15px;
}
.day-label {
  align-self: flex-start; 
  font-weight: bold;
  font-size: 18px;
  margin-bottom: 10px;
}
.drop-area {
  flex: 1; 
  width: 100%; 
  background-color: rgba(0, 0, 0, 0.2); 
  border-radius: 8px;
  margin-top: 10px; 
}

.menu-item {
    flex: 1;
    padding: 5px 5px 5px 5px;
    height: 500px;
    border-radius: 10px;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    color: white;
    font-weight: bold;
    font-size: 18px;
    box-shadow: 0px 4px 8px rgba(0, 0, 0, 0.2);
    transition: transform 0.3s ease, box-shadow 0.3s ease;
    position: relative;
}

.menu-item:nth-child(1) {
    background-color: #1E90FF; 
}

.menu-item:nth-child(2) {
    background-color: #87CEEB;
}

.menu-item:nth-child(3) {
    background-color: #8A2BE2; 
}

.menu-item:nth-child(4) {
    background-color: #7D3C98;
}

.menu-item:nth-child(5) {
    background-color: #4B0082; 
}

.menu-item:nth-child(6) {
    background-color: #40E0D0;
}

.menu-item:nth-child(7) {
    background-color: #6A5ACD; 
}

.menu-item:hover {
    transform: scale(1.05);
    box-shadow: 0px 6px 12px rgba(0, 0, 0, 0.4);
}

.menu-item p {
    margin: 0;
}
</style>

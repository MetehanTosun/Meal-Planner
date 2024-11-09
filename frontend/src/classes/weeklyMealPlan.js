export class WeeklyMealPlan{
    constructor(){
        this.days = {
            monday: [],
            tuesday: [],
            wednesday: [],
            thursday: [],
            friday: [],
            saturday: [],
            sunday: []
        };
    }

    removeRecipeFromDay(day, recipe){
        if(this.days[day]){
            var index = 0;
            this.days[day].array.forEach(recipe => {
                index++;
                if(recipe == recipe){

                }
            });
        }
    }
    clearDay(day){
        if(this.days[day]){
            this.days[day] = [];
        }
    }
    clearAll(){
        this.days = {
            monday: [],
            tuesday: [],
            wednesday: [],
            thursday: [],
            friday: [],
            saturday: [],
            sunday: []
        };
    }
    addRecipeToDay(day, recipe){}
    
}
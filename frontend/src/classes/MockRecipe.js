import Ingredient from "./Ingredient";
import Recipe from "./RecipeTemp"

class MockRecipe extends Recipe {
    constructor(name, ingredients, instructions, time) {
        super(name, ingredients, instructions, time)
    }
}

// MockRecipe Objects to test functionality of the App

const gnocchi = new Ingredient("Gnocchi", "150g");
const kirschtomaten = new Ingredient("Kirschtomaten", "50g");
const passierteTomaten = new Ingredient("Passierte Tomaten", "38ml");
const basilikum = new Ingredient("Basilikum", "10g");
const salz = new Ingredient("Salz", "10g");
const mozzarella = new Ingredient("Mozzarella", "31g");

const mockRecipeOne = new MockRecipe("Gnocchi-Auflauf", [gnocchi, kirschtomaten, passierteTomaten, basilikum, salz, mozzarella],
    "Den Backofen auf 200 Grad Ober- und Unterhitze vorheizen.Gnocchi ungekocht in eine Auflaufform geben. Tomaten halbieren und zu den Gnocchi geben.Passierte Tomaten unterheben und alles mit Salz, Pfeffer und Kräutern würzen. Dann alles mit Mozzarella-Scheiben bedecken.20 – 30 Minuten backen, je nach Größe deiner Gnocchi."
    , 25);

const weizenmehl405 = new Ingredient("Weizenmehl", "150g");
const zucker = new Ingredient("Zucker", "15g");
const backpulver = new Ingredient("Backpulver", "10g");
const ei = new Ingredient("Ei", "2");
const milch = new Ingredient("Milch", "120ml");

const mockRecipeTwo = new MockRecipe("American Pancakes",
    [weizenmehl405, zucker, backpulver, salz, ei, milch],
    "Das Mehl mit dem Zucker, Backpulver und Salz vermischen. Eier und Milch hinzugeben und alles zu einem glatten Teig verrühren.  Etwas Öl in eine Pfanne geben und auf mittlerer Stufe erhitzen. Je 2 EL Teig in die Pfanne geben. Die Pancakes sollten etwa einen Durchmesser von ca. 10 cm haben.Jeden Pancake ca. 2 Minuten ausbacken, bis sich kleine Bläschen bilden, dann wenden und von der anderen Seite ebenfalls so lange ausbacken. American Pancakes mit Beeren und Ahornsirup garnieren und servieren.",
    10);

const pizzateig = new Ingredient("Pizzateig", "250g");
const tomatensauce = new Ingredient("Tomatensauce", "100ml");
const mozzarellaPizza = new Ingredient("Mozzarella", "100g");
const basilikumPizza = new Ingredient("Basilikum", "5g");
const olivenoel = new Ingredient("Olivenöl", "1 EL");

const mockRecipeThree = new MockRecipe(
    "Margherita Pizza",
    [pizzateig, tomatensauce, mozzarellaPizza, basilikumPizza, olivenoel],
    "Backofen auf 220 Grad vorheizen. Den Pizzateig auf einem Blech ausrollen. Tomatensauce gleichmäßig auf dem Teig verteilen und mit Mozzarella belegen. Olivenöl über den Käse träufeln und mit Basilikum garnieren. Pizza für 10–15 Minuten backen, bis der Rand goldbraun und der Käse geschmolzen ist.",
    15
);

const spaghetti = new Ingredient("Spaghetti", "200g");
const eiCarbonara = new Ingredient("Eigelb", "2");
const parmesan = new Ingredient("Parmesan", "50g");
const pancetta = new Ingredient("Pancetta", "100g");
const schwarzerPfeffer = new Ingredient("Schwarzer Pfeffer", "nach Geschmack");

const mockRecipeFour = new MockRecipe(
    "Spaghetti Carbonara",
    [spaghetti, eiCarbonara, parmesan, pancetta, schwarzerPfeffer],
    "Die Spaghetti in Salzwasser kochen. In einer Pfanne die Pancetta anbraten, bis sie knusprig ist. Eier und Parmesan in einer Schüssel verquirlen. Die Spaghetti abgießen und mit der heißen Pancetta mischen. Die Ei-Parmesan-Mischung hinzufügen und gut vermengen, sodass die Spaghetti mit der Sauce bedeckt sind. Mit schwarzem Pfeffer abschmecken und servieren.",
    20
);

export const mockRecipes = [
    mockRecipeOne,
    mockRecipeTwo,
    mockRecipeThree,
    mockRecipeFour
];

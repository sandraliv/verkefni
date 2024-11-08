package com.hi.recipe.verkefni.ViewControllers;

/*public RecipeViewController(RecipeService recipeService, UserService userService){
        this.recipeService = recipeService;
        this.userService = userService;
    }

    /*public String recipeCard() {
       List<Recipe> cardRecipes = List.copyOf(recipeService.findAll());
       recipeService.getTitle();
       recipe.getDateAdded();
       recipe.getRating();
        return recipeCard();
    }*/


    /*@GetMapping("/recipes")
    public String index(Model model) {
        model.addAttribute("message", "Recipes");
        model.addAttribute("recipes", recipeService.findAllPaginated());
        model.addAttribute("login", "login");
        /*model.addAttribute("categories", recipeService.findByCategory(categories));*/
       /* return "FrontPage";
    }

   /* @GetMapping("/statuses")
    public String getRecipes(@RequestParam("page")Integer page, Model model) {
        if (page > 4) {
            model.addAttribute("statuses", Collections.<Recipe>emptyList());
            model.addAttribute("link", "/statuses?page=5");
            return "card";
        }
        var to = page * PAGE_LENGHT;
        var from = to - PAGE_LENGHT;
        model.addAttribute("card", recipeService.findAll());
        model.addAttribute("link", "/card?page=" + (page + 1));
        return "card";

    }*/


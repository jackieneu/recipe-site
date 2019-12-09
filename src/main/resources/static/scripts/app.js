$(document).ready(function () {
//    var ingredientIndex = $(".ingredient-row").length;
//    if(ingredientIndex === 0){
//        addIngredient();
//    }
//
//    var instructionIndex = $(".step-row").length;
//    if(instructionIndex === 0){
//        addInstruction();
//    }


    $('#category').change(function(){
        $('#categoryForm').submit();
    });

    $('#add-ingredient').on('click', function () {
        addIngredient();
    });

    $('#add-instruction').on('click', function () {
        addInstruction();
    });

    $('#mdiv-close-flash').on('click', function(){
        var flash = document.getElementById('flash-message');
        flash.style.display = 'none';
    });

    $('#signup-button').on('click', function(){
        var password1 = $("#password1").val();
        var password2 = $("#password2").val();
        if(password1 != "" && (password1 == password2)) {
            document.getElementById("signup-form").submit();
        } else {
            alert("Passwords don't match! Try re-typing them.");
            return false;
        }
    });

    function addIngredient(){
        ingredientIndex = $(".ingredient-row").length;
        var ingredientHTML = `
          <div class="ingredient-row">

            <div class="prefix-20 grid-30">
                <input type="hidden" name="ingredients[${ingredientIndex}].id}"/>
              <p>
                <input type="text" name="ingredients[${ingredientIndex}].item"/>
              </p>
            </div>

            <div class="grid-30">
              <p>
                <input type="text" name="ingredients[${ingredientIndex}].condition"/>
              </p>
            </div>

            <div class="grid-10 suffix-10">
              <p>
                <input type="number" name="ingredients[${ingredientIndex}].quantity" value="0"/>
              </p>
            </div>

          </div>
          `;

        $("#add-ingredient-before").before(ingredientHTML);
    }

   function addInstruction(){
        instructionIndex = $(".step-row").length;
        var instructionHTML = `
              <div class="step-row">

                <div class="prefix-5 grid-15">
                  <p>
                    <span>${instructionIndex + 1}. </span>
                  </p>
                </div>

                <div class="grid-80">
                  <p>
                    <input type="text" name="instructions[${instructionIndex}]"/>
                  </p>
                </div>

              </div>
          `;

        $("#add-instruction-before").before(instructionHTML);
    }
});
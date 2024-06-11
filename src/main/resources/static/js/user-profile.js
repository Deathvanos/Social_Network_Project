function showDiv(divId, button) {
    const divsToHide = document.querySelectorAll('.div-info');
    Array.prototype.forEach.call(divsToHide, function(div) {
        div.style.display = 'none';
    });

    const divToShow = document.getElementById(divId);
    divToShow.style.display = 'block';

    if (divId === 'player-information') {
        let playerId = button.id;

        $.ajax({
            type: 'GET',
            url: '/personnage/' + playerId,
            success: function(response) {
                $('#info-nom').text(response.lastName);
                $('#info-prenom').text(response.firstName);
                $('#info-race').text(response.raceString);
                $('#info-money').text(response.money);
                $('#info-level').text(response.level);
                $('#button-race').text('Fiche '+response.raceString);
                $('#info-description').text(response.description);
                $('#info-story').text(response.story);
            },
            error: function(xhr, status, error) {
                console.error('Error:', error);
            }
        });
    }
}

function clearSelection() {
    document.getElementById('race-select').selectedIndex = 0;
}

//delete personnage
$(document).ready(function() {
    $("#deleteForm").submit(function(event) {
        event.preventDefault();

        $.ajax({
                url: "/session/personnage",
                type: "GET",
                success: function(response) {
                    let personnageId = response.id

                    $.ajax({
                        url: "/personnage/" + personnageId,
                        type: "DELETE",
                        success: function(response) {
                            alert("Personnage supprimé avec succès!");
                            location.reload();
                        },
                        error: function(xhr, status, error) {
                            console.error(xhr.responseText);
                            alert("Erreur lors de la suppression du personnage.");
                        }
                    });
                },
                error: function(xhr, status, error) {
                    console.error(xhr.responseText);
                }
            });
    });
});

//modify user information
document.getElementById("modify-user-info-form").addEventListener("submit", function(event) {
    event.preventDefault();

    const email = document.getElementById("email").value;
    const emailError = document.getElementById("emailError");

    const username = document.getElementById('username').value;
    const usernameError = document.getElementById('usernameError');

    emailError.textContent = "";
    usernameError.textContent = "";

    // Check email uniqueness
    $.ajax({
        url: "/checkUnique",
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify({"email": email, "username": username}),
        success: function(response) {
            let uniqueError = false;
            if (response.existingEmail) {
                emailError.textContent = "Cette adresse mail est déjà utilisée";
                uniqueError = true;
            }

            if (response.existingUsername) {
                usernameError.textContent = "Ce pseudonym est déjà utilisé";
                uniqueError = true;
            }

            if (uniqueError) {return;}

            document.getElementById("signup-form").submit();
        },
        error: function(xhr, status, error) {
            console.error("Error checking uniqueness:", error);
        }
    });

});

//image cropper
$(document).ready(function () {
    let cropper;

    $('#imageModal').on('shown.bs.modal', function () {
        let image = document.getElementById('image');
        console.log(image);
        cropper = new Cropper(image, {
            aspectRatio: 3 / 4
        });
    });

    $('#imageModal').on('hidden.bs.modal', function () {
        cropper.destroy();
    });

    $('#closeButton').on('click', function() {
        $('#imageModal').modal('hide');
    });

    $(document).on('click', function (e) {
        if ($(e.target).hasClass('modal')) {
            $('#imageModal').modal('hide');
        }
    });

    $('#cropButton').click(function () {
        let croppedImageData = cropper.getCroppedCanvas().toDataURL('image/jpeg');
        $('#croppedImageData').val(croppedImageData);
        $('#imageModal').modal('hide');
        cropper.destroy();
    });

    $('#image-input').change(function (event) {
        let file = event.target.files[0];
        let reader = new FileReader();

        reader.onload = function (e) {
            $('#image').attr('src', e.target.result);
            $('#imageModal').modal('show');
        };

        reader.readAsDataURL(file);
    });
});

// user proof image
function validateForm() {
    let fileInput = document.getElementById('image-input');
    let file = fileInput.files[0];

    if (!file) {
        alert("Veuillez sélectionner une image.");
        return false;
    }
    return true;
}

$(document).ready(function(){
    $("#show-description-edit").click(function(){
        $("#info-description").toggle();
        $("#description-edit").toggle();
    });
    $("#show-story-edit").click(function(){
        $("#info-story").toggle();
        $("#story-edit").toggle();
    });
});
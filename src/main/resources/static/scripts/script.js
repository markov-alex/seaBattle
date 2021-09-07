const app = new Application({
    preparation: PreparationScene,
    gameScene: GameScene,
    endScene: EndScene
});
app.start("preparation");

// document.querySelector('[data-action="randomize"]').click();
// document.querySelector('[data-type="ready"]').disabled = false;
// document.querySelector('[data-type="ready"]').click();
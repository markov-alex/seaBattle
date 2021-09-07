class EndScene extends Scene {

    init() {
        this.endGame = this.endGame.bind(this);
    }

    async start() {
        document
            .querySelectorAll(".app-actions")
            .forEach((e) => e.classList.add("hidden"));
        document
            .querySelector('[data-scene="end"]')
            .classList.remove("hidden");
        if (this.app.nextMethod === "WINNER") {
            document.querySelector('[data-type="endStatus"]').innerHTML = "Вы выиграли";
        } else {
            document.querySelector('[data-type="endStatus"]').innerHTML = "Вы проиграли";
        }

        let response = await fetch(window.location.href + "/opponentShips");
        if (response === null) {
            alert("Ошибка аутентификации");
        } else {
            let ships = await response.json();
            const {opponent} = this.app;
            for (const ship of ships) {
                const cords = ship["coordinates"];
                const first = cords[0];
                const last = cords[cords.length - 1];
                const direction = first.x === last.x ? "column" : "row";
                const shipView = new ShipView(cords.length, direction, 0, 0);
                opponent.addShip(shipView, first.x, first.y);
            }
        }

        const exitButton = document.querySelector('[data-action="exit"]');
        exitButton.addEventListener("click", this.endGame);
    }

    async endGame() {
        const url = window.location.href + "/exit";
        let response = await fetch(url);
        let result = await response.json();

        if (result["nextMethod"] === "SUCCESS") {
            document.location.href = window.location.protocol + "//" + window.location.host + "/playground";
        } else {
            alert("Ошибка аутентификации");
        }
    }
}
class GameScene extends Scene {
    firstGet = true;
    firstPost = true;

    init() {
        this.surrender = this.surrender.bind(this);
    }

    async start() {
        document
            .querySelectorAll(".app-actions")
            .forEach((e) => e.classList.add("hidden"));
        document
            .querySelector('[data-scene="game"]')
            .classList.remove("hidden");
        const surrenderButton = document.querySelector('[data-action="surrender"]');
        surrenderButton.addEventListener("click", this.surrender);

        let response = await fetch(window.location.href + "/recovery");
        let result = await response.json();
        const playerShips = result["ships"];
        this.app.nextMethod = await result["nextMethod"];
        const {player, opponent} = this.app;
        player.removeAllShips();
        for (const ship of playerShips) {
            const cords = ship["coordinates"];
            const first = cords[0];
            const last = cords[cords.length - 1];
            const direction = first.x === last.x ? "column" : "row";
            const shipView = new ShipView(cords.length, direction, 0, 0);
            player.addShip(shipView, first.x, first.y);
        }
        this.addShotsOfResultOfShot(player, result, "opponentShots");
        this.addShotsOfResultOfShot(opponent, result, "playerShots");
    }

    async update() {
        const {mouse, player, opponent, nextMethod} = this.app;
        const cells = opponent.cells.flat();
        cells.forEach(cell => cell.classList.remove("battlefield-item__active"));

        const url = window.location.href + "/shot";
        if (nextMethod === "GET") {
            document.querySelector('[data-type="curStatus"]').innerHTML = "Ход противника";
            if (this.firstGet) {
                this.firstGet = false;
                let response = await fetch(url);
                let result = await response.json();
                this.addShotsOfResultOfShot(player, result, "resultOfShot");
                this.app.nextMethod = result["nextMethod"];
                this.firstGet = true;
            }
        } else if (nextMethod === "POST") {
            document.querySelector('[data-type="curStatus"]').innerHTML = "Ваш ход";
            if (isUnderPoint(mouse, opponent.table)) {
                const cell = cells.find((cell) => isUnderPoint(mouse, cell));
                if (cell) {
                    cell.classList.add("battlefield-item__active");
                    if (mouse.left && !mouse.pLeft) {
                        const x = parseInt(cell.dataset.x);
                        const y = parseInt(cell.dataset.y);
                        if (opponent.isNotShot(x, y)) {
                            this.firstPost = false;
                            let response = await fetch(url, {
                                method: "POST",
                                headers: {
                                    'Content-Type': 'application/json;charset=utf-8'
                                },
                                body: JSON.stringify({x, y})
                            });
                            let result = await response.json();
                            this.addShotsOfResultOfShot(opponent, result, "resultOfShot");
                            this.app.nextMethod = result["nextMethod"];
                            this.firstPost = true;
                        }
                    }
                }
            }
        } else if (nextMethod === "WINNER" || nextMethod === "LOOSER") {
            this.app.start("endScene");
        } else if (nextMethod != null) {
            alert("Ошибка аутентификации");
        }
    }

    addShotsOfResultOfShot(bf, result, name) {
        if (result[name]) {
            for (const resultOfShot of result[name]) {
                const coord = resultOfShot["coordinate"];
                let shot;
                if (resultOfShot["cellState"] === "SHOT") {
                    shot = new ShotView(coord.x, coord.y, "miss");
                } else if (resultOfShot["cellState"] === "BEATED_SHIP") {
                    shot = new ShotView(coord.x, coord.y, "wound");
                }
                bf.addShot(shot);
            }
        }
    }

    async surrender() {
        const url = window.location.href + "/surrender";
        let response = await fetch(url);
        let result = await response.json();
        this.app.nextMethod = result["nextMethod"];
    }
}
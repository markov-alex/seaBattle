const shipDataset = [
    {size: 4, direction: "row", startX: 10, startY: 345},
    {size: 3, direction: "row", startX: 10, startY: 390},
    {size: 3, direction: "row", startX: 120, startY: 390},
    {size: 2, direction: "row", startX: 10, startY: 435},
    {size: 2, direction: "row", startX: 88, startY: 435},
    {size: 2, direction: "row", startX: 167, startY: 435},
    {size: 1, direction: "row", startX: 10, startY: 480},
    {size: 1, direction: "row", startX: 55, startY: 480},
    {size: 1, direction: "row", startX: 100, startY: 480},
    {size: 1, direction: "row", startX: 145, startY: 480},
];

class PreparationScene extends Scene {
    draggedShip = null;
    draggedOffsetX = 0;
    draggedOffsetY = 0;

    init() {
        this.randomize = this.randomize.bind(this);
        this.manually = this.manually.bind(this);
        this.startGame = this.startGame.bind(this);
        this.manually();
    }

    async start() {
        document
            .querySelectorAll(".app-actions")
            .forEach((element) => element.classList.add("hidden"));
        document
            .querySelector('[data-scene="preparation"]')
            .classList.remove("hidden");

        const randomizeButton = document.querySelector('[data-action="randomize"]');
        randomizeButton.addEventListener("click", this.randomize);
        const manuallyButton = document.querySelector('[data-action="manually"]');
        manuallyButton.addEventListener("click", this.manually);
        const readyButton = document.querySelector('[data-type="ready"]');
        readyButton.addEventListener("click", this.startGame);

        let response = await fetch(window.location.href + "/isActive");
        let result = await response.json();
        if (result) {
            this.app.start("gameScene");
        }
    }

    update() {
        const {mouse, player} = this.app;
        // потенциальный захват корабля
        if (!this.draggedShip && mouse.left && !mouse.pLeft) {
            const ship = player.ships.find((ship) => ship.isUnder(mouse));
            if (ship) {
                const shipRect = ship.div.getBoundingClientRect();
                this.draggedOffsetX = mouse.x - shipRect.left;
                this.draggedOffsetY = mouse.y - shipRect.top;
                this.draggedShip = ship;
                ship.x = null;
                ship.y = null;
            }
        }
        // перемещение корабля
        if (mouse.left && this.draggedShip) {
            const {left, top} = player.root.getBoundingClientRect();
            const x = mouse.x - left - this.draggedOffsetX;
            const y = mouse.y - top - this.draggedOffsetY;
            this.draggedShip.div.style.left = `${x}px`;
            this.draggedShip.div.style.top = `${y}px`;
        }
        // постановка корабля на место
        if (!mouse.left && this.draggedShip) {
            const ship = this.draggedShip;
            this.draggedShip = null;

            const {left, top} = ship.div.getBoundingClientRect();
            const {width, height} = player.cells[0][0].getBoundingClientRect();
            const point = {
                x: left + width / 2,
                y: top + height / 2
            };

            const cell = player.cells.flat().find((cell) => isUnderPoint(point, cell));
            if (cell) {
                const x = parseInt(cell.dataset.x);
                const y = parseInt(cell.dataset.y);
                player.removeShip(ship);
                player.addShip(ship, x, y);
            } else {
                player.removeShip(ship);
                player.addShip(ship);
            }
        }
        // изменение направления корабля
        if (this.draggedShip && mouse.delta) {
            this.draggedShip.toggleDirection();
        }

        if (player.complete) {
            document.querySelector('[data-type="ready"]').disabled = false;
        } else {
            document.querySelector('[data-type="ready"]').disabled = true;
        }
    }

    stop() {
        const randomizeButton = document.querySelector('[data-action="randomize"]');
        randomizeButton.removeEventListener("click", this.randomize);
        const manuallyButton = document.querySelector('[data-action="manually"]');
        manuallyButton.removeEventListener("click", this.manually);
    }

    randomize() {
        const {player} = this.app;
        player.randomize(ShipView);
        for (let i = 0; i < 10; ++i) {
            const ship = player.ships[i];
            ship.startX = shipDataset[i].startX;
            ship.startY = shipDataset[i].startY;
        }
    }

    manually() {
        const {player} = this.app;
        player.removeAllShips();
        for (const {size, direction, startX, startY} of shipDataset) {
            const ship = new ShipView(size, direction, startX, startY);
            player.addShip(ship);
        }
    }

    async startGame() {
        const ships = this.app.player.ships;
        const requestCoords = [];
        for (const {x, y, size, direction} of ships) {
            const ship = [];
            const dx = direction === "row";
            const dy = direction === "column";
            for (let i = 0; i < size; ++i) {
                ship.push({
                    "x": x + i * dx,
                    "y": y + i * dy
                });
            }
            requestCoords.push(ship);
        }
        let response = await fetch(window.location.href + "/ready", {
            method: "POST",
            headers: {
                'Content-Type': 'application/json;charset=utf-8'
            },
            body: JSON.stringify(requestCoords)
        });
        let text = await response.json();
        this.app.nextMethod = text["nextMethod"];
        this.app.start("gameScene");
    }
}
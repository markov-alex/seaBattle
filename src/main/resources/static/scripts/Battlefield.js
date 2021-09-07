class Battlefield {
    ships = [];
    shots = [];

    __matrix = null;
    __changed = true;

    get matrix() {
        if (!this.__changed) {
            return this.__matrix;
        }
        const matrix = [];
        for (let y = 0; y < 10; ++y) {
            const row = [];
            for (let x = 0; x < 10; ++x) {
                const item = {
                    x, y,
                    ship: null,
                    free: true,
                    shoted: false,
                    wounded: false
                };
                row.push(item);
            }
            matrix.push(row);
        }
        for (const ship of this.ships) {
            if (!ship.placed) {
                continue;
            }
            const {x, y} = ship;
            const dx = ship.direction === "row";
            const dy = ship.direction === "column";
            for (let i = 0; i < ship.size; ++i) {
                const item = matrix[y + dy * i][x + dx * i];
                item.ship = ship;
            }
            for (let y = ship.y - 1; y < ship.y + ship.size * dy + 1 + dx; ++y) {
                for (let x = ship.x - 1; x < ship.x + ship.size * dx + 1 + dy; ++x) {
                    if (this.inField(x, y)) {
                        const item = matrix[y][x];
                        item.free = false;
                    }
                }
            }
        }
        for (const {x, y} of this.shots) {
            const item = matrix[y][x];
            item.shoted = true;

            if (item.ship) {
                item.wounded = true;
            }
        }
        this.__matrix = matrix;
        this.__changed = false;
        return this.__matrix;
    }

    get complete() {
        if (this.ships.length !== 10) {
            return false;
        }

        for (const ship of this.ships) {
            if (!ship.placed) {
                return false;
            }
        }

        return true;
    }

    inField(x, y) {
        const notCorrect = (n) => {
            return parseInt(n) !== n || isNaN(n) || n === Infinity || n === -Infinity;
        };
        if (notCorrect(x) || notCorrect(y)) {
            return false;
        }

        return 0 <= x && x < 10 && 0 <= y && y < 10;
    }

    addShip(ship, x, y) {
        if (this.ships.includes(ship)) {
            return false;
        }
        this.ships.push(ship);
        if (this.inField(x, y)) {
            const dx = ship.direction === "row";
            const dy = ship.direction === "column";
            let placed = true;

            for (let i = 0; i < ship.size; ++i) {
                const cx = x + dx * i;
                const cy = y + dy * i;
                if (!this.inField(cx, cy)) {
                    placed = false;
                    break;
                }
                const item = this.matrix[cy][cx];
                if (!item.free) {
                    placed = false;
                    break;
                }
            }
            if (placed) {
                Object.assign(ship, {x, y});
            }
        }
        this.__changed = true;
        return true;
    }

    removeShip(ship) {
        if (!this.ships.includes(ship)) {
            return false;
        }

        const index = this.ships.indexOf(ship);
        this.ships.splice(index, 1);
        ship.x = null;
        ship.y = null;
        this.__changed = true;
        return true;
    }

    removeAllShips() {
        const ships = this.ships.slice();

        for (const ship of ships) {
            this.removeShip(ship);
        }

        return ships.length;
    }

    addShot(shot) {
        if (!this.isNotShot(shot.x, shot.y)) {
            return false;
        }
        this.shots.push(shot);
        this.__changed = true;

        const matrix = this.matrix;
        const {x, y, variant} = shot;

        if (variant === "wound") {
            const {ship} = matrix[y][x];
            if (ship) {
                const dx = ship.direction === "row";
                const dy = ship.direction === "column";

                let killed = true;

                for (let i = 0; i < ship.size; ++i) {
                    const cy = ship.y + dy * i;
                    const cx = ship.x + dx * i;
                    const item = matrix[cy][cx];

                    if (!item.wounded) {
                        killed = false;
                        break;
                    }
                }

                if (killed) {
                    ship.killed = true;
                }
            }
        }
        this.__changed = true;
        return true;
    }

    removeShot() {
        this.__changed = true;
    }

    removeAllShots() {
        const shots = this.shots.slice();

        for (const shot of shots) {
            this.removeShot(shot);
        }

        return shots.length;
    }

    randomize(ShipClass = Ship) {
        this.removeAllShips();
        for (let size = 4; size >= 1; --size) {
            for (let n = 0; n < 5 - size; ++n) {
                const direction = getRandomFrom("row", "column");
                const ship = new ShipClass(size, direction);

                while (!ship.placed) {
                    const x = getRandomBetween(0, 9);
                    const y = getRandomBetween(0, 9);

                    this.removeShip(ship);
                    this.addShip(ship, x, y);
                }
            }
        }
    }

    isNotShot(x, y) {
        for (const shot of this.shots) {
            if (x === shot.x && y === shot.y) {
                return false;
            }
        }
        return true;
    }
}
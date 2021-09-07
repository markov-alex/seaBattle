class Shot {
    x = null;
    y = null;
    variant = null;

    constructor(x, y, variant) {
        Object.assign(this, {x, y, variant});
    }

    setVariant(variant) {
        this.variant = variant;
    }
}
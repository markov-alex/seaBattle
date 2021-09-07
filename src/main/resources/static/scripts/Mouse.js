class Mouse {
    // элемент DOM, над которым происходит отслеживание событий мыши
    element = null;
    //
    under = false;
    // предыдущий under
    pUnder = false;
    // текущая координата x мыши
    x = null;
    // текущая координата y мыши
    y = null;
    // предыдущий x
    pX = null;
    // предыдущий y
    pY = null;
    // булевская переменная, говорящая о нажатии левой кнопки мыши
    left = false;
    // предыдущий left
    pLeft = false;
    // значение скролла
    delta = 0;
    // предыдущее значение скролла
    pDelta = 0;

    constructor (element) {
        this.element = element;

        element.addEventListener('mousemove', e => {
            this.tick();

            this.update(e);
        });
        element.addEventListener('mouseenter', e => {
            this.tick();

            this.update(e);
        });
        element.addEventListener('mouseleave', e => {
            this.tick();

            this.update(e);
            this.under = false;
        });
        element.addEventListener('mousedown', e => {
            this.tick();

            this.update(e);

            if (e.button === 0) {
                this.left = true;
            }
        });
        element.addEventListener('mouseup', e => {
            this.tick();

            this.update(e);

            if (e.button === 0) {
                this.left = false;
            }
        });
        element.addEventListener('wheel', e => {
            this.tick();

            this.x = e.clientX;
            this.y = e.clientY;
            this.under = true;
            this.delta = e.deltaY > 0 ? 1 : -1;
        });
    }

    // обновление параметров мыши
    update = (e) => {
        this.x = e.clientX;
        this.y = e.clientY;
        this.under = true;
        this.delta = 0;
    }

    // эмулирует тик
    tick() {
        this.pUnder = this.under;
        this.pX = this.x;
        this.pY = this.y;
        this.pLeft = this.left;
        this.pDelta = this.delta;
        this.delta = 0;
    }
}
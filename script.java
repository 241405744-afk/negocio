document.addEventListener("DOMContentLoaded", function () {
    const botonesMenu = document.getElementById("menuBtn");
    const menu = document.getElementById("menu");
    const contador = document.getElementById("contador");
    const listaCarrito = document.getElementById("listaCarrito");
    const totalElemento = document.getElementById("total");
    const botonVaciar = document.getElementById("vaciarCarrito");
    const botonWhatsApp = document.getElementById("pedirWhatsApp");
    const mensaje = document.getElementById("mensaje");
    const botonSubir = document.getElementById("subir");
    const anio = document.getElementById("anio");

    const telefonoWhatsApp = "523411513585";
    let carrito = [];

    function formatoPrecio(cantidad) {
        return "$" + cantidad.toLocaleString("es-MX") + " MXN";
    }

    function guardarCarrito() {
        try {
            localStorage.setItem("carritoAbundancia", JSON.stringify(carrito));
        } catch (error) {
            console.warn("No fue posible guardar el carrito.");
        }
    }

    function recuperarCarrito() {
        try {
            const guardado = localStorage.getItem("carritoAbundancia");

            if (guardado) {
                const datos = JSON.parse(guardado);

                if (Array.isArray(datos)) {
                    carrito = datos.filter(function (producto) {
                        return producto &&
                            typeof producto.nombre === "string" &&
                            Number.isFinite(producto.precio) &&
                            producto.precio >= 0 &&
                            Number.isInteger(producto.cantidad) &&
                            producto.cantidad > 0;
                    });
                }
            }
        } catch (error) {
            carrito = [];
        }
    }

    function actualizarCarrito() {
        listaCarrito.replaceChildren();

        let total = 0;
        let cantidadProductos = 0;

        if (carrito.length === 0) {
            const textoVacio = document.createElement("p");
            textoVacio.className = "vacio";
            textoVacio.textContent = "Tu carrito está vacío.";
            listaCarrito.appendChild(textoVacio);
        }

        carrito.forEach(function (producto, indice) {
            const subtotal = producto.precio * producto.cantidad;

            total += subtotal;
            cantidadProductos += producto.cantidad;

            const item = document.createElement("div");
            item.className = "item-carrito";

            const informacion = document.createElement("div");

            const nombre = document.createElement("p");
            nombre.textContent = producto.nombre;

            const detalle = document.createElement("small");
            detalle.textContent =
                producto.cantidad + " × " + formatoPrecio(producto.precio);

            const subtotalTexto = document.createElement("strong");
            subtotalTexto.textContent = formatoPrecio(subtotal);

            informacion.appendChild(nombre);
            informacion.appendChild(detalle);

            const botonQuitar = document.createElement("button");
            botonQuitar.type = "button";
            botonQuitar.className = "quitar";
            botonQuitar.textContent = "Quitar";
            botonQuitar.setAttribute(
                "aria-label",
                "Quitar " + producto.nombre + " del carrito"
            );

            botonQuitar.addEventListener("click", function () {
                carrito.splice(indice, 1);
                mensaje.textContent = "";
                guardarCarrito();
                actualizarCarrito();
            });

            item.appendChild(informacion);
            item.appendChild(subtotalTexto);
            item.appendChild(botonQuitar);

            listaCarrito.appendChild(item);
        });

        contador.textContent = cantidadProductos;
        totalElemento.textContent = formatoPrecio(total);

        guardarCarrito();
    }

    document.querySelectorAll(".agregar").forEach(function (boton) {
        boton.addEventListener("click", function () {
            const nombre = boton.dataset.nombre;
            const precio = Number(boton.dataset.precio);

            if (!nombre || !Number.isFinite(precio) || precio < 0) {
                mensaje.textContent = "No se pudo agregar ese producto.";
                return;
            }

            const productoExistente = carrito.find(function (producto) {
                return producto.nombre === nombre;
            });

            if (productoExistente) {
                productoExistente.cantidad++;
            } else {
                carrito.push({
                    nombre: nombre,
                    precio: precio,
                    cantidad: 1
                });
            }

            mensaje.textContent = nombre + " se agregó al carrito.";
            actualizarCarrito();
        });
    });

    botonVaciar.addEventListener("click", function () {
        if (carrito.length === 0) {
            mensaje.textContent = "El carrito ya está vacío.";
            return;
        }

        carrito = [];
        mensaje.textContent = "Se vació el carrito.";
        actualizarCarrito();
    });

    botonWhatsApp.addEventListener("click", function () {
        if (carrito.length === 0) {
            mensaje.textContent = "Primero agrega productos al carrito.";
            return;
        }

        let texto = "Hola, Autolavado La Abundancia. Quiero consultar este pedido:\n\n";
        let total = 0;

        carrito.forEach(function (producto) {
            const subtotal = producto.precio * producto.cantidad;

            texto += producto.cantidad + " x " + producto.nombre +
                " - " + formatoPrecio(subtotal) + "\n";

            total += subtotal;
        });

        texto += "\nTotal calculado: " + formatoPrecio(total);
        texto += "\n¿Me pueden confirmar la disponibilidad?";

        const enlace = "https://wa.me/" + telefonoWhatsApp +
            "?text=" + encodeURIComponent(texto);

        window.open(enlace, "_blank", "noopener,noreferrer");
        mensaje.textContent =
            "Se abrió WhatsApp para que revises y envíes tu mensaje.";
    });

    botonesMenu.addEventListener("click", function () {
        const abierto = menu.classList.toggle("abierto");

        botonesMenu.setAttribute("aria-expanded", String(abierto));
        botonesMenu.textContent = abierto ? "✕ Cerrar" : "☰ Menú";
    });

    menu.querySelectorAll("a").forEach(function (enlace) {
        enlace.addEventListener("click", function () {
            menu.classList.remove("abierto");
            botonesMenu.setAttribute("aria-expanded", "false");
            botonesMenu.textContent = "☰ Menú";
        });
    });

    window.addEventListener("scroll", function () {
        if (window.scrollY > 300) {
            botonSubir.classList.add("visible");
        } else {
            botonSubir.classList.remove("visible");
        }
    });

    botonSubir.addEventListener("click", function () {
        window.scrollTo({
            top: 0,
            behavior: "smooth"
        });
    });

    anio.textContent = new Date().getFullYear();

    recuperarCarrito();
    actualizarCarrito();
});
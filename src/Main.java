import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {


        List<Ronda> rondas = new ArrayList<>();
        List<Pronostico> pronosticos = new ArrayList<>();
        List<String[]> datosPronostico = leerPronosticos();
        List<String[]> datosRondas = leerResultados();
        int puntoPorRonda = 2;
        int puntosPorAciertos = 1;

        try {
            String deDondeCargar = "src/configuracion.scv";
            List<String> lineas = Files.readAllLines(Paths.get(deDondeCargar));
            puntosPorAciertos = Integer.parseInt(lineas.get(0));
            puntoPorRonda = Integer.parseInt(lineas.get(1));


        } catch (IOException e) {

        }

        for (int r = 0; r < datosRondas.size(); r++) {
            boolean estaEnRonda = false;
            String[] lineaEntera = datosRondas.get(r);
            for (int j = 0; j < rondas.size(); j++) {
                Ronda rondaJugada = rondas.get(j);
                if (rondaJugada.nombre.equals(lineaEntera[1])) {
                    Partido partidoJugado = new Partido(lineaEntera[2], Integer.parseInt(lineaEntera[4]), Integer.parseInt(lineaEntera[5]), lineaEntera[3]);
                    rondaJugada.partidos.add(partidoJugado);
                    estaEnRonda = true;
                }
            }//buscando mariana en la lista
            if (!estaEnRonda) {
                Partido partidoJugado = new Partido(lineaEntera[2], Integer.parseInt(lineaEntera[4]), Integer.parseInt(lineaEntera[5]), lineaEntera[3]);
                Ronda nuevaRonda = new Ronda(lineaEntera[1], lineaEntera[0]);
                nuevaRonda.partidos.add(partidoJugado);
                rondas.add(nuevaRonda);

            }
        }
        for (int p = 0; p < datosPronostico.size(); p++) {

            boolean estaEnPronostico = false;

            String[] lineaEntera = datosPronostico.get(p);
            for (int m = 0; m < pronosticos.size(); m++) {
                Pronostico pronosticado = pronosticos.get(m);
                if (pronosticado.nombre.equals(lineaEntera[0])) {
                    Partido partidoJugado = new Partido(Integer.parseInt(lineaEntera[5]),
                            lineaEntera[3],
                            lineaEntera[4]);
                    pronosticado.partidos.add(partidoJugado);

                    estaEnPronostico = true;
                }
            }//buscando mariana en la lista
            if (!estaEnPronostico) {
                Partido partidoJugado = new Partido(Integer.parseInt(lineaEntera[5]),
                        lineaEntera[3],
                        lineaEntera[4]);
                Pronostico nuevoPronostico = new Pronostico(lineaEntera[0]);
                nuevoPronostico.partidos.add(partidoJugado);
                pronosticos.add(nuevoPronostico);

            }


        }

        for (int p = 0; p < pronosticos.size(); p++) {
            Pronostico estePronostico = pronosticos.get(p);
            int puntoGanado = 0;
            for (int pt = 0; pt < estePronostico.partidos.size(); pt++) {
                Partido partidoPronostico = estePronostico.partidos.get(pt);


                for (int r = 0; r < rondas.size(); r++) {
                    Ronda estaRonda = rondas.get(r);
                    int puntoAciertoRonda = 0;// por acertar toda la ronda
                    for (int rp = 0; rp < estaRonda.partidos.size(); rp++) {
                        Partido rondaPartido = estaRonda.partidos.get(rp);

                        if (partidoPronostico.equipo1.equals(rondaPartido.equipo1) && partidoPronostico.equipo2.equals(rondaPartido.equipo2)) {
                            if (partidoPronostico.resultado == rondaPartido.obtenerResultado()) {
                                puntoGanado = puntoGanado + puntosPorAciertos;
                                puntoAciertoRonda++;
                            }
                        }


                    }//por acertar toda la ronda gana 2 puntos
                    if (puntoAciertoRonda == estaRonda.partidos.size()) {

                        puntoGanado = puntoGanado + puntoPorRonda;
                    }

                }
            }
            System.out.println(estePronostico.nombre + " suma " + puntoGanado + " puntos ");
        }
    }

    // Va a devolver una Lista con un arreglo de String que va a contener:
    // Posicion 0: Nombre de la persona
    // Posicion 1: Fase
    // Posicion 2: Ronda
    // Posicion 3: Nombre equipo 1
    // Posicion 4: Nombre equipo 2
    // Posicion 5: Ganador
    public static List<String[]> leerPronosticos() {
        List<String[]> pronosticos = new ArrayList<>();

        // Cargamos el Driver
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Error cargando el driver");
        }

        try {
            // Creamos la conexión
            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://sql10.freemysqlhosting.net:3306/sql10612293",
                    "sql10612293", "ACwUKDKvbY");
            Statement stmt = con.createStatement();

            // El Query que vamos a correr
            ResultSet rs = stmt.executeQuery("SELECT NOMBRE, FASE, RONDA, E1.EQUIPO AS EQUIPO_1, E2.EQUIPO AS EQUIPO_2, GANADOR FROM PRONOSTICOS P JOIN RESULTADOS R on P.ID_RESULTADO = R.ID_RESULTADO JOIN EQUIPOS E1 on R.ID_EQUIPO_1 = E1.ID_EQUIPO JOIN EQUIPOS E2 on R.ID_EQUIPO_2 = E2.ID_EQUIPO");
            while (rs.next()) {
                String[] fila = new String[6];
                fila[0] = rs.getString("NOMBRE");
                fila[1] = rs.getString("FASE");
                fila[2] = rs.getString("RONDA");
                fila[3] = rs.getString("EQUIPO_1");
                fila[4] = rs.getString("EQUIPO_2");
                fila[5] = rs.getString("GANADOR");
                pronosticos.add(fila);
            }
            con.close();
        } catch (SQLException e) {
            System.out.println("Error con SQL");
        }

        return pronosticos;
    }

    // Va a devolver una Lista con un arreglo de String que va a contener:
    // Posicion 0: Ronda
    // Posicion 1: Fase
    // Posicion 2: Nombre equipo 1
    // Posicion 3: Nombre equipo 2
    // Posicion 4: Goles equipo 1
    // Posicion 5: Goles equipo 2
    public static List<String[]> leerResultados() {
        List<String[]> resultados = new ArrayList<>();


        // Cargamos el Driver
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Error cargando el driver");
        }

        try {
            // Creamos la conexión
            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://sql10.freemysqlhosting.net:3306/sql10612293",
                    "sql10612293", "ACwUKDKvbY");
            Statement stmt = con.createStatement();

            // El Query que vamos a correr
            ResultSet rs = stmt.executeQuery("SELECT FASE, RONDA, E1.EQUIPO AS EQUIPO_1, E2.EQUIPO AS EQUIPO_2, GOLES_1, GOLES_2 FROM RESULTADOS R JOIN EQUIPOS E1 on R.ID_EQUIPO_1 = E1.ID_EQUIPO JOIN EQUIPOS E2 on R.ID_EQUIPO_2 = E2.ID_EQUIPO");
            while (rs.next()) {
                String[] fila = new String[6];
                fila[0] = rs.getString("FASE");
                fila[1] = rs.getString("RONDA");
                fila[2] = rs.getString("EQUIPO_1");
                fila[3] = rs.getString("EQUIPO_2");
                fila[4] = rs.getString("GOLES_1");
                fila[5] = rs.getString("GOLES_2");
                resultados.add(fila);
            }
            con.close();
        } catch (SQLException e) {
            System.out.println("Error con SQL");
        }

        return resultados;
    }
}


























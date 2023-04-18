

public class Partido {
    int resultado;
    String equipo1;
    int golesEquipo1;
    int golesEquipo2;
    String equipo2;


    public Partido(String equipo1,int golesEquipo1,int golesEquipo2,String equipo2){
        this.equipo1 = equipo1;
        this.golesEquipo1 =golesEquipo1;
        this.golesEquipo2 =golesEquipo2;
        this.equipo2 = equipo2;


    }
    public Partido(int resultado,String equipo1,String equipo2){
        this.equipo1 = equipo1;
        this.equipo2 = equipo2;
        this.resultado = resultado;


    }

    public int obtenerResultado() {
        if (this.golesEquipo1 > this.golesEquipo2) {
            return   1;
        } else if (this.golesEquipo2 > this.golesEquipo1) {
            return  2;
        } else {
            return 0;


        }


        }
    }









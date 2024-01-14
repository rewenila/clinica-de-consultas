public class Consulta {
    Paciente paciente;
    String data;
    String hora;
    String especialidade;

    Consulta(Paciente paciente, String data, String hora, String especialidade) {
        this.paciente = paciente;
        this.data = data;
        this.hora = hora;
        this.especialidade = especialidade;
    }
}

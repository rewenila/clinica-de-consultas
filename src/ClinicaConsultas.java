import java.io.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Scanner;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClinicaConsultas {

    static ArrayList<Paciente> pacientesCadastrados = new ArrayList<>();
    static ArrayList<Consulta> consultasAgendadas = new ArrayList<>();
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        carregarDados();

        System.out.println("Bem vindo ao sistema de agendamentos da Clínica de Consultas Ágil.");
        System.out.println("A clínica funciona das 08:00 às 20:00, com consultas de meia em meia hora.");

        int opcao;
        do {
            System.out.println();
            System.out.println("Menu Principal:");
            System.out.println("1. Cadastrar Paciente");
            System.out.println("2. Marcar Consulta");
            System.out.println("3. Cancelar Consulta");
            System.out.println("4. Sair");
            System.out.print("Escolha uma opção: ");
            opcao = scanner.nextInt();
            scanner.nextLine();

            System.out.println();

            switch (opcao) {
                case 1:
                    cadastrarPaciente();
                    break;
                case 2:
                    marcarConsulta();
                    break;
                case 3:
                    cancelarConsulta();
                    break;
                case 4:
                    System.out.println("Saindo do programa. Até logo!");
                    break;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }
        } while (opcao != 4);

        salvarDados();
    }

    static void carregarDados() {
        try (BufferedReader br = new BufferedReader(new FileReader("dados.txt"))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                String[] dados = linha.split(";");

                if (dados[0].equals("Paciente")) {
                    Paciente paciente = new Paciente(dados[1], dados[2]);
                    pacientesCadastrados.add(paciente);
                } else if (dados[0].equals("Consulta")) {
                    Paciente paciente = buscarPacientePorTelefone(dados[1]);
                    if (paciente != null) {
                        Consulta consulta = new Consulta(paciente, dados[2], dados[3], dados[4]);
                        consultasAgendadas.add(consulta);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Erro ao carregar dados. Iniciando com listas vazias.");
        }
    }

    static void salvarDados() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("dados.txt"))) {
            for (Paciente paciente : pacientesCadastrados) {
                bw.write("Paciente;" + paciente.nome + ";" + paciente.telefone);
                bw.newLine();
            }

            for (Consulta consulta : consultasAgendadas) {
                bw.write("Consulta;" + consulta.paciente.telefone + ";" + consulta.data + ";" +
                        consulta.hora + ";" + consulta.especialidade);
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Erro ao salvar dados.");
        }
    }

    static Paciente buscarPacientePorTelefone(String telefone) {
        for (Paciente paciente : pacientesCadastrados) {
            if (paciente.telefone.equals(telefone)) {
                return paciente;
            }
        }
        return null;
    }

    static void cadastrarPaciente() {
        System.out.print("Digite o nome completo do paciente (ex: João da Silva): ");
        String nome = scanner.nextLine();

        System.out.print("Digite o telefone do paciente (ex: 123456789): ");
        String telefone = scanner.nextLine();

        if (!isTelefoneValido(telefone)) {
            System.out.println("Telefone inválido. Certifique-se de que contém exatamente 9 dígitos numéricos.");
            return;
        }

        // Verifica se o paciente já está cadastrado
        for (Paciente paciente : pacientesCadastrados) {
            if (paciente.telefone.equals(telefone)) {
                System.out.println("Paciente já cadastrado!");
                return;
            }
        }

        // Caso não esteja cadastrado, adiciona à lista de pacientes cadastrados
        Paciente novoPaciente = new Paciente(nome, telefone);
        pacientesCadastrados.add(novoPaciente);
        System.out.println("Paciente cadastrado com sucesso!");
    }

    static void marcarConsulta() {
        if (pacientesCadastrados.isEmpty()) {
            System.out.println("Não há pacientes cadastrados. Cadastre um paciente primeiro.");
            return;
        }

        System.out.println("Lista de Pacientes Cadastrados:");
        for (int i = 0; i < pacientesCadastrados.size(); i++) {
            System.out.println((i + 1) + ". " + pacientesCadastrados.get(i).nome);
        }

        System.out.print("Escolha o número correspondente ao paciente: ");
        int numeroPaciente = scanner.nextInt();
        scanner.nextLine();

        if (numeroPaciente < 1 || numeroPaciente > pacientesCadastrados.size()) {
            System.out.println("Número de paciente inválido. Tente novamente.");
            return;
        }

        Paciente pacienteSelecionado = pacientesCadastrados.get(numeroPaciente - 1);

        System.out.print("Digite a data da consulta (formato dd/mm/yyyy): ");
        String data = scanner.nextLine();

        System.out.print("Digite a hora da consulta (formato hh:mm): ");
        String hora = scanner.nextLine();

        // Verifica se a data e/ou hora são retroativas
        if (!isDataValida(data, hora)) {
            System.out.println("Não é possível agendar consultas retroativas. Escolha outra data e/ou hora.");
            return;
        }

        // Verifica se o hora informada está dentro do horário de funcionamento da clínica e
        // atende ao formato HH:00 ou HH:30
        if (!isHorarioPossivel(hora)) {
            System.out.println("Não é possível agendar consultas nesse horário. Tente novamente.");
            return;
        }

        // Verifica se a data e hora já estão agendadas
        for (Consulta consulta : consultasAgendadas) {
            if (consulta.data.equals(data) && consulta.hora.equals(hora)) {
                System.out.println("Essa data e hora já estão agendadas. Escolha outro horário.");
                return;
            }
        }

        System.out.print("Digite a especialidade desejada: ");
        String especialidade = scanner.nextLine();

        // Agenda a consulta
        Consulta novaConsulta = new Consulta(pacienteSelecionado, data, hora, especialidade);
        consultasAgendadas.add(novaConsulta);
        System.out.println("Consulta marcada com sucesso!");
    }

    static void cancelarConsulta() {
        if (consultasAgendadas.isEmpty()) {
            System.out.println("Não há consultas agendadas para cancelar.");
            return;
        }

        System.out.println("Lista de Consultas Agendadas:");
        for (int i = 0; i < consultasAgendadas.size(); i++) {
            Consulta consulta = consultasAgendadas.get(i);
            System.out.println((i + 1) + ". " + consulta.paciente.nome + " - Data: " + consulta.data +
                    ", Hora: " + consulta.hora + ", Especialidade: " + consulta.especialidade);
        }

        System.out.print("Escolha o número correspondente à consulta que deseja cancelar: ");
        int numeroConsulta = scanner.nextInt();
        scanner.nextLine();

        if (numeroConsulta < 1 || numeroConsulta > consultasAgendadas.size()) {
            System.out.println("Número de consulta inválido. Tente novamente.");
            return;
        }

        Consulta consultaCancelada = consultasAgendadas.remove(numeroConsulta - 1);
        System.out.println("Consulta cancelada com sucesso para o paciente " + consultaCancelada.paciente.nome);
    }

    static boolean isTelefoneValido(String telefone) {
        String regex = "\\d{9}";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(telefone.replace(" ",""));

        return matcher.matches();
    }

    static boolean isDataValida(String data, String hora) {
        DateTimeFormatter formatterData = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter formatterHora = DateTimeFormatter.ofPattern("HH:mm");

        LocalDate dataFornecida = LocalDate.parse(data, formatterData);
        LocalDate dataAtual = LocalDate.now();

        if (dataFornecida.isEqual(dataAtual)) {
            LocalTime horaFornecida = LocalTime.parse(hora, formatterHora);
            LocalTime horaAtual = LocalTime.now();

            return horaFornecida.isAfter(horaAtual);
        }

        return dataFornecida.isAfter(dataAtual);
    }

    static boolean isHorarioPossivel(String hora) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        LocalTime horaFornecida = LocalTime.parse(hora, formatter);

        if (horaFornecida.getMinute() != 0 && horaFornecida.getMinute() != 30) {
            return false;
        }

        if (horaFornecida.getHour() < 8 || horaFornecida.getHour() >= 20) {
            return false;
        }

        return true;
    }
}

# Clínica de Consultas Ágil

Bem-vindo à Clínica de Consultas Ágil! Este é um sistema simples para agendamento de consultas em uma clínica local 
fictícia.

## Funcionalidades

1. **Cadastrar Paciente:** Cadastre novos pacientes fornecendo nome e telefone. Evita duplicidade de cadastros.

2. **Marcações de Consultas:** Agende consultas, escolhendo um paciente e especificando data, hora e especialidade 
   desejada.

3. **Cancelamento de Consultas:** Remova consultas agendadas, liberando horários previamente reservados.

4. **Persistência de Dados:** As informações de pacientes e consultas são salvas para persistência de dados entre 
   execuções.

## Tratamento de Erros

- Evita cadastro duplicado de pacientes.
- Impede agendamento em dias/horas já ocupados.
- Consultas não podem ser marcadas retroativamente.

## Limitações
- É possivel agendar apenas uma consulta por horário, independentemente da especialidade desejada.
- Não é realizada verificação de que a especialidade escolhida é uma especialidade válida e existente na clínica.

## Como Usar

1. Clone este repositório.
2. Execute o programa Java.
3. Siga as instruções do menu para cadastrar pacientes, agendar consultas, cancelar consultas ou sair.

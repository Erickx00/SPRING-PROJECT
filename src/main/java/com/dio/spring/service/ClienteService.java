package com.dio.spring.service;

import com.dio.spring.client.ViaCepClient;
import com.dio.spring.exceptions.CepNotFoundException;
import com.dio.spring.exceptions.ClienteNotFoundException;
import com.dio.spring.model.Cliente;
import com.dio.spring.model.Endereco;
import com.dio.spring.repository.ClienteRepository;
import com.dio.spring.repository.EnderecoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final EnderecoRepository enderecoRepository;
    private final ViaCepClient viaCepClient;

    @Autowired
    public ClienteService(ClienteRepository clienteRepository,
                           EnderecoRepository enderecoRepository,
                           ViaCepClient viaCepClient) {
        this.clienteRepository = clienteRepository;
        this.enderecoRepository = enderecoRepository;
        this.viaCepClient = viaCepClient;
    }

    public Iterable<Cliente> buscarTodos() {
        return clienteRepository.findAll();
    }

    public Cliente buscarPorId(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteNotFoundException("Cliente não encontrado com id: " + id));
    }

    public Cliente inserir(Cliente cliente) {
        cliente.setEndereco(buscarOuCriarEndereco(cliente.getEndereco()));
        return clienteRepository.save(cliente);
    }

    public Cliente atualizar(Long id, Cliente cliente) {
        Cliente existente = buscarPorId(id);
        existente.setNome(cliente.getNome());
        existente.setEndereco(buscarOuCriarEndereco(cliente.getEndereco()));
        return clienteRepository.save(existente);
    }

    public void deletar(Long id) {
        Cliente existente = buscarPorId(id);
        clienteRepository.delete(existente);
    }

    private Endereco buscarOuCriarEndereco(Endereco endereco) {
        if (endereco == null || endereco.getCep() == null || endereco.getCep().isBlank()) {
            return null;
        }
        String cep = endereco.getCep().replace("-", "").trim();
        return enderecoRepository.findById(cep)
                .orElseGet(() -> {
                    Endereco encontrado = viaCepClient.buscarEnderecoPorCep(cep);
                    if (encontrado == null || encontrado.getLogradouro() == null) {
                        throw new CepNotFoundException("CEP não encontrado: " + cep);
                    }
                    encontrado.setCep(cep);
                    return enderecoRepository.save(encontrado);
                });
    }
}

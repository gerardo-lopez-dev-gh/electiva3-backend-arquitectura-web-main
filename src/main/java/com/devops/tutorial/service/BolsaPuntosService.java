package com.devops.tutorial.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.devops.tutorial.dto.BolsaPuntosClienteDTO;
import com.devops.tutorial.dto.ClienteBolsaPuntosDTO;
import com.devops.tutorial.model.BolsaPuntos;
import com.devops.tutorial.model.Cliente;
import com.devops.tutorial.model.ReglaAsignacion;
import com.devops.tutorial.repository.BolsaPuntosRepository;
import com.devops.tutorial.repository.ClienteRepository;
import com.devops.tutorial.repository.ParamVencimientoRepository;
import com.devops.tutorial.repository.ReglaAsignacionRepository;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BolsaPuntosService {
    @Autowired
    private BolsaPuntosRepository bolsaPuntosRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ReglaAsignacionRepository reglaAsignacionRepository;

    @Autowired
    private ParamVencimientoRepository paramVencimientoRepository;

    public BolsaPuntos asignarPuntos(Long clienteId, Double montoOperacion) {

        ReglaAsignacion reglaAsignacion = reglaAsignacionRepository
            .findByMontoOperacion(montoOperacion);
        
        if (reglaAsignacion == null) {
            throw new RuntimeException("No existe una regla de asignación para el monto proporcionado.");
        }

        int puntosAsignados = (int) (montoOperacion * reglaAsignacion.getEquivalenciaPunto());

        BolsaPuntos bolsaPuntos = new BolsaPuntos();
        bolsaPuntos.setCliente(clienteRepository.findById(clienteId).orElseThrow(() -> new RuntimeException("Cliente no encontrado")));
        bolsaPuntos.setParamVencimiento(paramVencimientoRepository.findFirstOrderedById());
        bolsaPuntos.setFechaAsignacion(new Date());
        bolsaPuntos.setPuntajeAsignado(puntosAsignados);
        bolsaPuntos.setPuntajeUtilizado(0);
        bolsaPuntos.setSaldoPuntos(puntosAsignados);
        bolsaPuntos.setMontoOperacion(montoOperacion);
        bolsaPuntos.setReglaAsignacionId(reglaAsignacion.getId());

        return bolsaPuntosRepository.save(bolsaPuntos);
    }

    public BolsaPuntos guardarBolsa(BolsaPuntos bolsa) {
        return bolsaPuntosRepository.save(bolsa);
    }

    public Optional<BolsaPuntos> obtenerBolsaPorId(Long id) {
        return bolsaPuntosRepository.findById(id);
    }

    public List<BolsaPuntos> obtenerTodasLasBolsas() {
        return bolsaPuntosRepository.findAll();
    }

    public void eliminarBolsa(Long id) {
        bolsaPuntosRepository.deleteById(id);
    }
    //PUNTO 9-PROCESO PARA CORRER UN PROCESO SCHEDULADO, ES PARAMETRIZABLE POR EL APPLICATION PROPPERTIES
    @Scheduled(fixedRateString = "${config.processRate}")
    public void actualizarEstadoBolsasPuntosVencidas() {
        Date ahora = new Date();
        List<BolsaPuntos> bolsasVencidas = bolsaPuntosRepository.findBolsasPuntosVencidas(ahora);

        for (BolsaPuntos bolsa : bolsasVencidas) {
            bolsa.setSaldoPuntos(0);
            bolsaPuntosRepository.save(bolsa);
        }
    }

    //PUNTO 7-OBTENER LOS CLIENTES QUE TIENEN PUNTOS POR VENCER EN CADA X DIAS.
    public List<BolsaPuntosClienteDTO> obtenerPuntosAVencerEnDias(int dias, BigInteger clienteId) {
        List<Object[]> results;
        if (clienteId == null) {
            results = bolsaPuntosRepository.findClientesConPuntosAVencer(dias, null);
        } else {
            results = bolsaPuntosRepository.findClientesConPuntosAVencer(dias, clienteId);
        }
        return results.stream().map(result -> {
            BigInteger clienteIdResult = (BigInteger) result[0];
            String nombreCliente = (String) result[1];
            String apellidoCliente = (String) result[2];
            
            Cliente cliente = new Cliente();
            cliente.setId(clienteIdResult.longValue());
            cliente.setNombre(nombreCliente);
            cliente.setApellido(apellidoCliente);
            
            return new BolsaPuntosClienteDTO(cliente);
        }).collect(Collectors.toList());
    }
    
    //PUNTO 7-Bolsa de puntos por: cliente, rango de puntos.
    public List<Object> findClienteBolsaPuntosByClienteId(Long clienteId) {
        List<Object> result = bolsaPuntosRepository.findClienteBolsaPuntosByClienteId(clienteId);
        return result;
        
    }

}

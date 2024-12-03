package com.devops.tutorial.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.devops.tutorial.model.BolsaPuntos;
import com.devops.tutorial.model.UsoPuntos;
import com.devops.tutorial.model.UsoPuntosDetalle;
import com.devops.tutorial.repository.UsoPuntosRepository;
import com.devops.tutorial.repository.BolsaPuntosRepository;
import com.devops.tutorial.repository.ClienteRepository;
import com.devops.tutorial.repository.ConceptoRepository;
import com.devops.tutorial.repository.ParamVencimientoRepository;
import com.devops.tutorial.repository.UsoPuntosDetalleRepository;

import java.math.BigInteger;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;


@Service
public class UsoPuntosService {
    @Autowired
    private UsoPuntosRepository usoPuntosRepository;

    @Autowired
    private UsoPuntosDetalleRepository usoPuntosDetalleRepository;

    @Autowired
    private BolsaPuntosRepository bolsaPuntosRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ConceptoRepository conceptoRepository;

    @Autowired
    private ParamVencimientoRepository paramVencimientoRepository;



    public UsoPuntos guardarUso(Long idCliente, Date fechaCarga, int puntos, Long conceptoId) {
        
        List<Object[]> bolsasDisponibles = bolsaPuntosRepository.findPuntosDisponibles(idCliente, fechaCarga);
        
        UsoPuntos usoPuntos = new UsoPuntos();
        usoPuntos.setCliente(clienteRepository.findById(idCliente).orElseThrow(() -> new RuntimeException("Cliente no encontrado")));
        usoPuntos.setFecha(fechaCarga);
        usoPuntos.setPuntajeUtilizado(puntos);
        usoPuntos.setConceptoUso(conceptoRepository.findById(conceptoId).orElseThrow(() -> new RuntimeException("Concepto no encontrado")));
        
        usoPuntos = usoPuntosRepository.save(usoPuntos);
    
        int puntosRestantes = puntos;
    
        for (Object[] bolsaObj : bolsasDisponibles) {
            BolsaPuntos bolsa = new BolsaPuntos();
            bolsa.setId(((BigInteger) bolsaObj[0]).longValue());
            
            Timestamp timestampFechaAsignacion = (Timestamp) bolsaObj[1];
            bolsa.setFechaAsignacion(new Date(timestampFechaAsignacion.getTime()));
            
            bolsa.setMontoOperacion((Double) bolsaObj[2]);
            bolsa.setPuntajeAsignado((Integer) bolsaObj[3]);
            bolsa.setPuntajeUtilizado((Integer) bolsaObj[4]);
            bolsa.setSaldoPuntos((Integer) bolsaObj[5]);
            bolsa.setCliente(clienteRepository.findById(((BigInteger) bolsaObj[6]).longValue()).orElse(null));
            bolsa.setParamVencimiento(paramVencimientoRepository.findById(((BigInteger) bolsaObj[7]).longValue()).orElse(null));
            bolsa.setReglaAsignacionId(((BigInteger) bolsaObj[8]).longValue());
    
            int disponible = ((Number) bolsaObj[9]).intValue(); // disponible calculado en la consulta
    
            if (puntosRestantes <= 0) {
                break;
            }
    
            int puntosAUtilizar = Math.min(puntosRestantes, disponible);
            puntosRestantes -= puntosAUtilizar;
            bolsa.setPuntajeUtilizado(bolsa.getPuntajeUtilizado() + puntosAUtilizar);
            bolsa.setSaldoPuntos(bolsa.getSaldoPuntos() - puntosAUtilizar);
            bolsaPuntosRepository.save(bolsa);
    
            UsoPuntosDetalle detalle = new UsoPuntosDetalle();
            detalle.setUsoPuntos(usoPuntos);
            detalle.setBolsaPuntos(bolsa);
            detalle.setPuntajeUtilizado(puntosAUtilizar);
            usoPuntosDetalleRepository.save(detalle);
        }
    
        if (puntosRestantes > 0) {
            throw new RuntimeException("No hay suficientes puntos disponibles");
        }
    
        return usoPuntos;
    }

    public Optional<UsoPuntos> obtenerUsoPorId(Long id) {
        return usoPuntosRepository.findById(id);
    }

    public List<UsoPuntos> obtenerTodosLosUsos() {
        return usoPuntosRepository.findAll();
    }

    public void eliminarUso(Long id) {
        usoPuntosRepository.deleteById(id);
    }

    public UsoPuntosDetalle guardarUsoDetalle(UsoPuntosDetalle usoPuntosDetalle) {
        return usoPuntosDetalleRepository.save(usoPuntosDetalle);
    }

    public void eliminarUsoDetalle(Long id) {
        usoPuntosDetalleRepository.deleteById(id);
    }

    public List<UsoPuntos> obtenerUsosPorConceptoFechaCliente(String conceptoUso, Long clienteId) {
        if (conceptoUso == null || clienteId == null) {
            throw new IllegalArgumentException("No se cargaron los parametros necesarios.");
        }
        
        return usoPuntosRepository.findByConceptoUsoAndFechaUsoAndClienteId(conceptoUso, clienteId);
    };
    
    public void utilizarPuntos(){
        
    }
}

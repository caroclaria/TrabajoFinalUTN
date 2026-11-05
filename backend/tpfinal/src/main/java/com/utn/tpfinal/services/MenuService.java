package com.utn.tpfinal.services;

import com.utn.tpfinal.dtos.regimen.request.MenuRequestDTO;
import com.utn.tpfinal.dtos.regimen.response.IngredienteResponseDTO;
import com.utn.tpfinal.dtos.regimen.response.MenuResponseDTO;
import com.utn.tpfinal.dtos.regimen.response.PlatoResponseDTO;
import com.utn.tpfinal.entities.Menu;
import com.utn.tpfinal.entities.Plato;
import com.utn.tpfinal.repositories.MenuRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MenuService {
    private MenuRepository menuRepository;



    public MenuService(MenuRepository menuRepository) {
        this.menuRepository = menuRepository;
    }

    //create
    public Menu createMenu(MenuRequestDTO request) {
        Menu menu = new Menu(request.numeroLista(), request.tipoRegimen(), request.turnoComida(), request.platos());
        Menu saved = menuRepository.save(menu);
        return saved;
    }
    //getbyid

    //getall

    //patch

    //delete



}

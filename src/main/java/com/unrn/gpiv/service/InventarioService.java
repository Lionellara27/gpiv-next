package com.unrn.gpiv.service;

import com.unrn.gpiv.model.Item;
import com.unrn.gpiv.repository.ItemRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class InventarioService {

    private final ItemRepository itemRepository;

    // Inyección por constructor (la forma más segura y pro en Spring)
    public InventarioService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    // 🎯 ESTE ES EL MÉTODO QUE USA LA VISTA PARA LLENAR EL COMBOBOX
    public List<Item> obtenerTodosLosItemsCatalogo() {
        return itemRepository.findAll();
    }

    // Método para cuando el administrador quiera crear nuevos "Moldes" (Ej: Dar de alta un nuevo tipo de tractor)
    public void guardarItem(Item item) {
        itemRepository.save(item);
    }

    // Método para borrar un ítem del catálogo si se cargó por error
    public void eliminarItem(Long id) {
        if (itemRepository.existsById(id)) {
            itemRepository.deleteById(id);
        }
    }
}

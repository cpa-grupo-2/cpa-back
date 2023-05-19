package com.biopark.cpa.repository.grupo;


  
    import org.springframework.data.jpa.repository.JpaRepository;
    import org.springframework.stereotype.Repository;

import com.biopark.cpa.entities.grupos.Questoes;
    
    @Repository
    public interface QuestoesRepository extends JpaRepository<Questoes, Long> {
        
    }
    

    


package org.ufsc.si.livraria.helper;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;

public class OntologyHelper {
	private OWLOntology ontology;
    private OWLReasoner reasoner;

    public OntologyHelper(String ontologyPath) throws OWLOntologyCreationException {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        ontology = manager.loadOntologyFromOntologyDocument(new File(ontologyPath));

        OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
        reasoner = reasonerFactory.createReasoner(ontology);
    }

    public List<OWLNamedIndividual> getIndividualsOf(String className) {
        OWLClass owlClass = ontology.classesInSignature()
                .filter(it -> it.getIRI().getShortForm().equals(className))
                .findFirst()
                .get();

        return reasoner.getInstances(owlClass, false)
                .entities()
                .collect(Collectors.toList());
    }

    public Map<OWLNamedIndividual, List<OWLObjectPropertyAssertionAxiom>> mapAxioms(List<OWLNamedIndividual> subjects, String property) {
        Map<OWLNamedIndividual, List<OWLObjectPropertyAssertionAxiom>> map = new HashMap<>();

        subjects.forEach(it -> {
            String usuario = it.getIRI().getShortForm();
            List<OWLObjectPropertyAssertionAxiom> axioms = this.mapAxioms(usuario, property);
            map.put(it, axioms);
        });
        return map;
    }

    private List<OWLObjectPropertyAssertionAxiom> mapAxioms(String subject, String property) {
        return ontology.axioms(AxiomType.OBJECT_PROPERTY_ASSERTION)
                .filter(it -> it.getSubject().toString().contains(subject) &&
                        it.getProperty().getNamedProperty().getIRI().getShortForm().equals(property))
                .collect(Collectors.toList());
    }

    public Map<OWLNamedIndividual, String> mapCategorias(List<OWLNamedIndividual> livros) {
        Map<OWLNamedIndividual, String> categoriasPorLivro = new HashMap<>();

        livros.forEach(livro -> {
            OWLDataPropertyAssertionAxiom categoria = ontology.axioms(AxiomType.DATA_PROPERTY_ASSERTION)
                    .filter(it -> it.getSubject().equals(livro))
                    .findFirst()
                    .get();
            categoriasPorLivro.put(livro, categoria.getObject().toString());
        });

        return categoriasPorLivro;
    }
}
package com.patroservicos.PatroServicos.service;

import com.patroservicos.PatroServicos.model.User;

/**
 * Interface de serviço para gerenciar usuários.
 * Define os contratos para salvar usuários, solicitar profissional e aprovar profissionais.
 */
public interface IUserService {
	
	/**
	 * Salva um novo usuário no banco de dados.
	 * Encripta a senha e atribui roles padrão se necessário.
	 * 
	 * @param usuario Objeto do usuário a ser salvo
	 * @return ID do usuário salvo
	 */
	public Integer saveUser(User usuario);

	/**
	 * Solicita que um usuário seja promovido a profissional.
	 * Marca o tipo de conta como "profissional_pendente".
	 * 
	 * @param email E-mail do usuário
	 */
	public void requestProfessional(String email);

	/**
	 * Aprova um usuário como profissional.
	 * Altera o tipo de conta para "cliente_profissional" e adiciona ROLE_PROFESSIONAL.
	 * 
	 * @param usuarioId ID do usuário a ser aprovado
	 */
	public void approveProfessional(Integer usuarioId);
}
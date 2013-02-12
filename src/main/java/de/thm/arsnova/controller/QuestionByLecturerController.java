/*
 * Copyright (C) 2012 THM webMedia
 *
 * This file is part of ARSnova.
 *
 * ARSnova is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ARSnova is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.thm.arsnova.controller;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import de.thm.arsnova.entities.Answer;
import de.thm.arsnova.entities.Question;
import de.thm.arsnova.exceptions.NotFoundException;
import de.thm.arsnova.services.IQuestionService;

@Controller
@RequestMapping("/question/bylecturer")
public class QuestionByLecturerController extends AbstractController {

	public static final Logger LOGGER = LoggerFactory.getLogger(QuestionByLecturerController.class);

	@Autowired
	private IQuestionService questionService;

	@RequestMapping(value = "/{questionId}", method = RequestMethod.GET)
	@ResponseBody
	public final Question getQuestion(
			@PathVariable final String questionId,
			final HttpServletResponse response
	) {
		Question question = questionService.getQuestion(questionId);
		if (question != null) {
			return question;
		}

		response.setStatus(HttpStatus.NOT_FOUND.value());
		return null;
	}

	@RequestMapping(
			value = "/", 
			method = RequestMethod.POST
			)
	@ResponseBody
	public final Question postQuestion(
			@RequestBody final Question question,
			final HttpServletResponse response
	) {

		if (questionService.saveQuestion(question) != null) {
			response.setStatus(HttpStatus.CREATED.value());
			return question;
		}

		response.setStatus(HttpStatus.BAD_REQUEST.value());
		
		return null;
	}

	@RequestMapping(
			value = "/session/{sessionkey}/question/{questionId}", 
			method = RequestMethod.PUT
			)
	@ResponseBody
	public final void updateQuestion(
			@PathVariable final String sessionkey,
			@PathVariable final String questionId,
			@RequestBody final Question question,
			final HttpServletResponse response
	) {
		response.setStatus(HttpStatus.NO_CONTENT.value());
		
		/* TODO: Not yet implemented! The following code has been copy-and-pasted from postQuestion */
		/*
		if (!sessionkey.equals(question.getSession())) {
			response.setStatus(HttpStatus.PRECONDITION_FAILED.value());
			return null;
		}

		if (questionService.saveQuestion(question) != null) {
			response.setStatus(HttpStatus.CREATED.value());
			return question;
		}

		response.setStatus(HttpStatus.BAD_REQUEST.value());
		
		return null;
		*/
	}


	@RequestMapping(
			value = "/session/{sessionkey}/question/{questionId}/publish", 
			method = RequestMethod.POST
			)
	@ResponseBody
	public final void publishQuestion(
			@PathVariable final String sessionkey,
			@PathVariable final String questionId,
			@RequestParam(required = false) final Boolean publish,
			@RequestBody final Question question,
			final HttpServletResponse response
	) {
		/* TODO: Not yet implemented! */
		response.setStatus(HttpStatus.NO_CONTENT.value());
	}
	

	@RequestMapping(
			value = { "/list" },
			method = RequestMethod.GET
	)
	@ResponseBody
	public final List<Question> getSkillQuestions(
			@RequestParam final String sessionkey,
			final HttpServletResponse response
	) {
		List<Question> questions = questionService.getSkillQuestions(sessionkey);
		if (questions == null || questions.isEmpty()) {
			response.setStatus(HttpStatus.NOT_FOUND.value());
			return null;
		}
		LOGGER.info(questions.toString());
		return questions;
	}

	@RequestMapping(value = "/count", method = RequestMethod.GET)
	@ResponseBody
	public final int getSkillQuestionCount(@RequestParam final String sessionkey, final HttpServletResponse response) {
		LOGGER.debug(sessionkey);
		return questionService.getSkillQuestionCount(sessionkey);
	}

	@RequestMapping(value = "/ids", method = RequestMethod.GET)
	@ResponseBody
	public final List<String> getQuestionIds(
			@RequestParam final String sessionkey,
			final HttpServletResponse response
	) {
		List<String> questions = questionService.getQuestionIds(sessionkey);
		if (questions == null || questions.isEmpty()) {
			throw new NotFoundException();
		}
		LOGGER.info(questions.toString());
		return questions;
	}

	@RequestMapping(value = "/{questionId}", method = RequestMethod.DELETE)
	@ResponseBody
	public final void deleteAnswersAndQuestion(
			@PathVariable final String questionId,
			final HttpServletResponse response
	) {
		questionService.deleteQuestion(questionId);
	}

	@RequestMapping(value = "/unanswered", method = RequestMethod.GET)
	@ResponseBody
	public final List<String> getUnAnsweredSkillQuestions(
			@RequestParam final String sessionkey,
			final HttpServletResponse response
	) {
		List<String> answers = questionService.getUnAnsweredQuestions(sessionkey);
		if (answers == null || answers.isEmpty()) {
			response.setStatus(HttpStatus.NO_CONTENT.value());
			return null;
		}
		return answers;
	}

	/**
	 * returns a JSON document which represents the given answer of a question.
	 *
	 * @param sessionKey
	 *            Session Keyword to which the question belongs to
	 * @param questionId
	 *            CouchDB Question ID for which the given answer should be
	 *            retrieved
	 * @return JSON Document of {@link Answer} or {@link NotFoundException}
	 * @throws NotFoundException
	 *             if wrong session, wrong question or no answer was given by
	 *             the current user
	 * @throws ForbiddenException
	 *             if not logged in
	 */
	@RequestMapping(value = "/{questionId}/myanswer", method = RequestMethod.GET)
	@ResponseBody
	public final Answer getMyAnswer(
			@PathVariable final String questionId,
			final HttpServletResponse response
	) {
		Answer answer = questionService.getMyAnswer(questionId);
		if (answer == null) {
			throw new NotFoundException();
		}
		return answer;
	}

	/**
	 * returns a list of {@link Answer}s encoded as a JSON document for a given
	 * question id. In this case only {@link Answer} <tt>questionId</tt>,
	 * <tt>answerText</tt>, <tt>answerSubject</tt> and <tt>answerCount</tt>
	 * properties are set
	 *
	 * @param sessionKey
	 *            Session Keyword to which the question belongs to
	 * @param questionId
	 *            CouchDB Question ID for which the given answers should be
	 *            retrieved
	 * @return List<{@link Answer}> or {@link NotFoundException}
	 * @throws NotFoundException
	 *             if wrong session, wrong question or no answers was given
	 * @throws ForbiddenException
	 *             if not logged in
	 */
	@RequestMapping(value = "/{questionId}/answers", method = RequestMethod.GET)
	@ResponseBody
	public final List<Answer> getAnswers(
			@PathVariable final String questionId,
			final HttpServletResponse response
	) {
		List<Answer> answers = questionService.getAnswers(questionId);
		if (answers == null || answers.isEmpty()) {
			throw new NotFoundException();
		}
		return answers;
	}

	/**
	 *
	 * @param sessionKey
	 *            Session Keyword to which the question belongs to
	 * @param questionId
	 *            CouchDB Question ID for which the given answers should be
	 *            retrieved
	 * @return count of answers for given question id
	 * @throws NotFoundException
	 *             if wrong session or wrong question
	 * @throws ForbiddenException
	 *             if not logged in
	 */
	@RequestMapping(value = "/{questionId}/answercount", method = RequestMethod.GET)
	@ResponseBody
	public final int getAnswerCount(
			@PathVariable final String questionId,
			final HttpServletResponse response
	) {
		return questionService.getAnswerCount(questionId);
	}

	@RequestMapping(value = "/{questionId}/freetextanswers", method = RequestMethod.GET)
	@ResponseBody
	public final List<Answer> getFreetextAnswers(
			@PathVariable final String questionId,
			final HttpServletResponse response
	) {
		return questionService.getFreetextAnswers(questionId);
	}

	@RequestMapping(value = "/myanswers", method = RequestMethod.GET)
	@ResponseBody
	public final List<Answer> getMyAnswers(
			@RequestParam final String sessionkey,
			final HttpServletResponse response
	) {
		return questionService.getMytAnswers(sessionkey);
	}

	@RequestMapping(value = "/answercount", method = RequestMethod.GET)
	@ResponseBody
	public final int getTotalAnswerCount(
			@RequestParam final String sessionkey,
			final HttpServletResponse response
	) {
		return questionService.getTotalAnswerCount(sessionkey);
	}

}
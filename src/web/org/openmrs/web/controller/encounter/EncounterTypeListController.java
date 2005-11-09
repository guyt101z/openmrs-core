package org.openmrs.web.controller.encounter;

import java.util.List;
import java.util.Locale;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.EncounterType;
import org.openmrs.api.APIException;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.EncounterService;
import org.openmrs.context.Context;
import org.openmrs.web.Constants;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.view.RedirectView;

public class EncounterTypeListController extends SimpleFormController {
	
    /** Logger for this class and subclasses */
    protected final Log log = LogFactory.getLog(getClass());
    
	/**
	 * 
	 * Allows for Integers to be used as values in input tags.
	 *   Normally, only strings and lists are expected 
	 * 
	 * @see org.springframework.web.servlet.mvc.BaseCommandController#initBinder(javax.servlet.http.HttpServletRequest, org.springframework.web.bind.ServletRequestDataBinder)
	 */
	protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
		super.initBinder(request, binder);
        binder.registerCustomEditor(java.lang.Integer.class,
                new CustomNumberEditor(java.lang.Integer.class, true));
	}

	/**
	 * 
	 * The onSubmit function receives the form/command object that was modified
	 *   by the input form and saves it to the db
	 * 
	 * @see org.springframework.web.servlet.mvc.SimpleFormController#onSubmit(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object, org.springframework.validation.BindException)
	 */
	protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object obj, BindException errors) throws Exception {
		
		HttpSession httpSession = request.getSession();
		Context context = (Context) httpSession.getAttribute(Constants.OPENMRS_CONTEXT_HTTPSESSION_ATTR);
		Locale locale = request.getLocale();
		String view = getFormView();
		if (context != null && context.isAuthenticated()) {
			String[] encounterTypeList = request.getParameterValues("encounterTypeId");
			AdministrationService as = context.getAdministrationService();
			EncounterService os = context.getEncounterService();
			
			String success = "";
			String error = "";
			
			for (String p : encounterTypeList) {
				//TODO convenience method deleteEncounterType(Integer) ??
				try {
					as.deleteEncounterType(os.getEncounterType(Integer.valueOf(p)));
					if (!success.equals("")) success += "<br>";
					success += p + " deleted";
				}
				catch (APIException e) {
					log.warn(e);
					if (!error.equals("")) error += "<br>";
					error += p + " cannot be deleted";
				}
			}
			
			view = getSuccessView();
			if (!success.equals(""))
				httpSession.setAttribute(Constants.OPENMRS_MSG_ATTR, success);
			if (!error.equals(""))
				httpSession.setAttribute(Constants.OPENMRS_ERROR_ATTR, error);
		}
			
		return new ModelAndView(new RedirectView(view));
	}

	/**
	 * 
	 * This is called prior to displaying a form for the first time.  It tells Spring
	 *   the form/command object to load into the request
	 * 
	 * @see org.springframework.web.servlet.mvc.AbstractFormController#formBackingObject(javax.servlet.http.HttpServletRequest)
	 */
    protected Object formBackingObject(HttpServletRequest request) throws ServletException {

    	HttpSession httpSession = request.getSession();
		Context context = (Context) httpSession.getAttribute(Constants.OPENMRS_CONTEXT_HTTPSESSION_ATTR);
		
		//default empty Object
		List<EncounterType> encounterTypeList = new Vector<EncounterType>();
		
		//only fill the Object is the user has authenticated properly
		if (context != null && context.isAuthenticated()) {
			EncounterService os = context.getEncounterService();
	    	encounterTypeList = os.getEncounterTypes();
		}
    	
        return encounterTypeList;
    }
    
}